use std::env;
use std::fs;
use std::path::{Path, PathBuf};
use std::process::Command;
mod cli;
mod dir_util;
extern crate clap;
use clap::ArgMatches;
use cli::{Args, ProjectType};
use dir_util::{AncestorsUtil, SafeFileName};

fn main() -> Result<(), String> {
    // parse CLI arguments
    let args = cli::parse_args();

    // figure out current directory, current directory file name, relation to git repository
    let dir_info = detect_git_dir().map_err(|e| e.to_string())?;

    // make sure we're on the defalut branch
    ensure_on_default_branch(&args)?;

    // make sure there are no pending changes
    if !args.ignore_pending_changes() {
        ensure_no_pending_changes()?;
    }

    // make sure we're on latest and greatest
    git_fetch()?;
    git_pull()?;

    // TODO support project_type == "detect"
    let project_type = args
        .project_type()
        .expect("Auto-detection not implemented yet, must provide -t argument");
    // TODO validate semver
    // TODO support patch|minor|major
    // TODO support validating against package.json to avoid semver gaps
    match project_type {
        ProjectType::NPM => {
            // bump version in package.json and package-lock.json via external command
            npm_version(&args)?;
        }
        ProjectType::PIP => {
            pip_version(&args).map_err(|e| e.to_string())?;
        }
    }

    // generate changelog
    git_cliff(&args, &dir_info)?;

    // commit changes
    git_commit(&args, &dir_info)?;

    // tag the commit
    git_tag(&args, &dir_info)?;

    // push
    if args.push() {
        git_push()?;
    }
    // TODO support snapshot / development version
    // npm_version(version)?; set snapshot version
    //git_commit(version)?;
    //git_push()?;
    Ok(())
}

#[derive(Debug)]
struct DirInfo {
    current_dir: PathBuf,
    repository_dir: Option<PathBuf>,
}

impl DirInfo {
    fn current_dir_name(&self) -> &str {
        self.current_dir.safe_file_name()
    }
}

fn detect_git_dir() -> Result<DirInfo, std::io::Error> {
    let current_dir = env::current_dir()?;
    let is_at_git_root = has_git_dir(&current_dir);
    let mut found_git = false;
    let mut repository_dir: Option<PathBuf> = None;
    if is_at_git_root {
        found_git = true;
    } else {
        let mut parent = current_dir.parent();
        while !found_git && parent.is_some() {
            if has_git_dir(parent.unwrap()) {
                found_git = true;
                repository_dir = Some(parent.unwrap().to_owned());
            } else {
                parent = parent.unwrap().parent();
            }
        }
    }
    if found_git {
        Ok(DirInfo {
            current_dir,
            repository_dir,
        })
    } else {
        Err(std::io::Error::new(
            std::io::ErrorKind::Other,
            "Not in a git repository",
        ))
    }
}

fn has_git_dir(path: &Path) -> bool {
    let git_dir = path.join(".git");
    git_dir.is_dir()
}

fn ensure_on_default_branch(matches: &ArgMatches) -> Result<(), String> {
    let default_branch = git_default_branch(matches)?;
    let current_branch = git_current_branch()?;
    if current_branch == default_branch {
        Ok(())
    } else {
        Err("Not on the default branch".to_owned())
    }
}

fn ensure_no_pending_changes() -> Result<(), String> {
    let has_pending_changes = git_has_pending_changes()?;
    if has_pending_changes {
        Err("There are pending changes".to_owned())
    } else {
        Ok(())
    }
}

fn git_fetch() -> Result<String, String> {
    Command::new("git").arg("fetch").arg("-p").arg("-t").run()
}

fn git_pull() -> Result<String, String> {
    Command::new("git").arg("pull").run()
}

fn git_default_branch(matches: &ArgMatches) -> Result<String, String> {
    // git symbolic-ref refs/remotes/origin/HEAD
    // refs/remotes/origin/trunk
    let origin = matches.value_of("git-remote-name").unwrap_or_default();
    let prefix = format!("refs/remotes/{}/", origin);
    let head = format!("{}HEAD", prefix);
    let output = Command::new("git").arg("symbolic-ref").arg(head).run()?;
    let (_, second) = output.split_at(prefix.len());
    Ok(second.to_owned())
}

fn git_current_branch() -> Result<String, String> {
    // git rev-parse --abbrev-ref HEAD
    // trunk
    Command::new("git")
        .arg("rev-parse")
        .arg("--abbrev-ref")
        .arg("HEAD")
        .run()
}

fn git_has_pending_changes() -> Result<bool, String> {
    // git diff --quiet
    match Command::new("git").arg("diff").arg("--quiet").output() {
        Ok(output) => Ok(!output.status.success()),
        Err(err) => Err(err.to_string()),
    }
}

fn npm_version(args: &ArgMatches) -> Result<String, String> {
    // npm version --no-git-tag-version patch
    // TODO support non-Windows
    Command::new("cmd")
        .arg("/C")
        .arg("npm.cmd")
        .arg("version")
        .arg("--no-git-tag-version")
        .arg(args.version())
        .run()
}

fn git_cliff(args: &ArgMatches, dir_info: &DirInfo) -> Result<String, String> {
    // git-cliff --include-path 'generator-python-kamino/*' -r .. -o CHANGELOG.md -t 0.0.1
    let include_path = git_cliff_include_path(dir_info);
    let repository = git_cliff_repository(dir_info);
    let version = args.version();
    let mut cmd = &mut Command::new("git-cliff");
    if include_path != "" {
        cmd = cmd.arg("--include-path").arg(include_path);
    }
    if repository != "" {
        cmd = cmd.arg("-r").arg(repository);
    }
    cmd.arg("-o")
        .arg("CHANGELOG.md")
        .arg("-t")
        .arg(version)
        .run()
}

fn git_commit(args: &ArgMatches, dir_info: &DirInfo) -> Result<String, String> {
    let version = args.version();
    let project = if dir_info.repository_dir.is_some() {
        dir_info.current_dir_name()
    } else {
        ""
    };
    let msg = if project != "" {
        format!(
            "chore(release): prepare for version {} of {}",
            version, project
        )
    } else {
        format!("chore(release): prepare for version {}", version)
    };
    Command::new("git")
        .arg("commit")
        .arg("-a") // TODO only commit files we know are supposed to have changed
        .arg("-m")
        .arg(msg)
        .run()
}

fn git_tag(args: &ArgMatches, dir_info: &DirInfo) -> Result<String, String> {
    // TODO if possible add the changelog of only the current release in the git tag
    let version = args.version();
    let project = if dir_info.repository_dir.is_some() {
        dir_info.current_dir_name()
    } else {
        ""
    };
    let msg = if project != "" {
        format!("Releasing version {} of {}", version, project)
    } else {
        format!("Releasing version {}", version)
    };
    let tag = if project != "" {
        format!("{}/{}", project, version)
    } else {
        format!("v{}", version)
    };
    Command::new("git")
        .arg("tag")
        .arg("-a")
        .arg("-m")
        .arg(msg)
        .arg(tag)
        .run()
}

fn git_push() -> Result<String, String> {
    Command::new("git").arg("push").arg("--follow-tags").run()
}

pub trait SimpleCommand {
    fn run(&mut self) -> Result<String, String>;
}

impl SimpleCommand for Command {
    fn run(&mut self) -> Result<String, String> {
        match self.output() {
            Ok(output) => {
                if output.status.success() {
                    match String::from_utf8(output.stdout) {
                        Ok(s) => Ok(s.trim().to_owned()),
                        Err(err) => Err(err.to_string()),
                    }
                } else {
                    match String::from_utf8(output.stderr) {
                        Ok(s) => Err(s),
                        Err(_) => Err("Command failed".to_owned()),
                    }
                }
            }
            Err(err) => Err(err.to_string()),
        }
    }
}

fn git_cliff_include_path(dir_info: &DirInfo) -> String {
    match &dir_info.repository_dir {
        Some(repository_dir) => {
            let ancestors = dir_info.current_dir.ancestors_until(repository_dir);
            if ancestors.is_empty() {
                String::new()
            } else {
                let mut result: String = ancestors
                    .iter()
                    .map(|x| x.safe_file_name().to_owned())
                    .reduce(|accum, item| format!("{}/{}", accum, item))
                    .unwrap_or_default();
                result.push_str("/*");
                result
            }
        }
        None => String::new(),
    }
}

fn git_cliff_repository(dir_info: &DirInfo) -> String {
    match &dir_info.repository_dir {
        Some(repository_dir) => {
            let ancestors = dir_info.current_dir.ancestors_until(repository_dir);
            ancestors
                .iter()
                .map(|_| "..".to_owned())
                .reduce(|accum, item| format!("{}/{}", accum, item))
                .unwrap_or_default()
        }
        None => String::new(),
    }
}

fn pip_version(args: &ArgMatches) -> Result<(), PipError> {
    let setup_cfg_contents = String::from_utf8(fs::read("setup.cfg")?)?;
    let module_name = pip_get_module_name_from_setup_cfg(&setup_cfg_contents)?;
    let module_file = format!("{}/__init__.py", module_name);
    let module_contents = String::from_utf8(fs::read(&module_file)?)?;
    let new_contents = pip_update_version(&module_contents, args.version());
    fs::write(&module_file, new_contents)?;
    Ok(())
}

fn pip_get_module_name_from_setup_cfg(contents: &str) -> Result<&str, PipError> {
    let prefix = "version = attr: ";
    let version_line = contents
        .lines()
        .filter(|line| line.starts_with(prefix))
        .next()
        .ok_or(PipError::VersionLineNotFound)?;
    let (_, version_source) = version_line.split_at(prefix.len());
    let postfix = ".__version__";
    if version_source.ends_with(postfix) {
        let (result, _) = version_source.split_at(version_source.len() - postfix.len());
        Ok(result)
    } else {
        Err(PipError::VersionLineNotSupported)
    }
}

fn pip_update_version(contents: &str, version: &str) -> String {
    let mut result = String::new();
    for line in contents.lines() {
        if line.starts_with("__version__") {
            result.push_str("__version__ = \"");
            result.push_str(version);
            result.push('"');
        } else {
            result.push_str(&line);
        }
        result.push('\n');
    }
    result
}

#[derive(Debug)]
enum PipError {
    VersionLineNotFound,
    VersionLineNotSupported,
    IOError(std::io::Error),
    Utf8Error(std::string::FromUtf8Error),
}

impl std::fmt::Display for PipError {
    fn fmt(
        &self,
        formatter: &mut std::fmt::Formatter<'_>,
    ) -> std::result::Result<(), std::fmt::Error> {
        match self {
            Self::VersionLineNotFound => {
                formatter.write_str("Could not find version line in setup.cfg")
            }
            Self::VersionLineNotSupported => {
                formatter.write_str("The version line in setup.cfg is not supported")
            }
            Self::IOError(e) => e.fmt(formatter),
            Self::Utf8Error(e) => e.fmt(formatter),
        }
    }
}

impl From<std::io::Error> for PipError {
    fn from(err: std::io::Error) -> Self {
        Self::IOError(err)
    }
}

impl From<std::string::FromUtf8Error> for PipError {
    fn from(err: std::string::FromUtf8Error) -> Self {
        Self::Utf8Error(err)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_git_cliff_include_path_no_repository_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir(),
            repository_dir: None,
        };
        assert_eq!(git_cliff_include_path(&dir_info), "");
    }

    #[test]
    fn test_git_cliff_include_path_with_repository_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir().join("child"),
            repository_dir: Some(env::temp_dir()),
        };
        assert_eq!(git_cliff_include_path(&dir_info), "child/*");
    }

    #[test]
    fn test_git_cliff_include_path_with_grand_child_repository_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir().join("child").join("grand-child"),
            repository_dir: Some(env::temp_dir()),
        };
        assert_eq!(git_cliff_include_path(&dir_info), "child/grand-child/*");
    }

    #[test]
    fn test_git_cliff_include_path_with_repository_dir_accidentally_equal_to_current_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir(),
            repository_dir: Some(env::temp_dir()),
        };
        assert_eq!(git_cliff_include_path(&dir_info), "");
    }

    #[test]
    fn test_git_cliff_repository_no_repository_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir(),
            repository_dir: None,
        };
        assert_eq!(git_cliff_repository(&dir_info), "");
    }

    #[test]
    fn test_git_cliff_repository_with_repository_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir().join("child"),
            repository_dir: Some(env::temp_dir()),
        };
        assert_eq!(git_cliff_repository(&dir_info), "..");
    }

    #[test]
    fn test_git_cliff_repository_with_grand_child_repository_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir().join("child").join("grand-child"),
            repository_dir: Some(env::temp_dir()),
        };
        assert_eq!(git_cliff_repository(&dir_info), "../..");
    }

    #[test]
    fn test_git_cliff_repository_with_repository_dir_accidentally_equal_to_current_dir() {
        let dir_info = DirInfo {
            current_dir: env::temp_dir(),
            repository_dir: Some(env::temp_dir()),
        };
        assert_eq!(git_cliff_repository(&dir_info), "");
    }

    #[test]
    fn test_pip_get_module_name_from_setup_cfg() {
        let contents = r"
[metadata]
name = instarepo
version = attr: instarepo.__version__
";
        let module_name = pip_get_module_name_from_setup_cfg(contents).unwrap();
        assert_eq!(module_name, "instarepo");
    }

    #[test]
    fn test_pip_update_version() {
        let old_contents = r#"
# commented line
__version__ = "0.1.0"
"#;
        let new_contents = pip_update_version(old_contents, "0.2.0");
        let expected_contents = r#"
# commented line
__version__ = "0.2.0"
"#;
        assert_eq!(new_contents, expected_contents);
    }
}
