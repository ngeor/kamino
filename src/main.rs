use std::env;
use std::path::{Path, PathBuf};
use std::process::Command;
mod cli;
mod dir_util;
mod file_version;
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
    file_version::set_version(project_type, args.version())?;

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

    // snapshot / development version
    if args.snapshot_version() != "" {
        // set version
        file_version::set_version(project_type, args.snapshot_version())?;

        // commit
        git_commit(&args, &dir_info)?;

        if args.push() {
            git_push()?;
        }
    }
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
}
