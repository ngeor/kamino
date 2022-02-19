use std::process::Command;
mod cli;
extern crate clap;
use clap::ArgMatches;

fn main() -> Result<(), String> {
    let matches = cli::parse_args();
    let project_type = matches.value_of("type").unwrap_or_default();
    if project_type != "npm" {
        // TODO support project_type == "detect"
        return Err(format!(
            "type argument {} not implemented yet",
            project_type
        ));
    }
    // TODO validate semver
    // TODO support patch|minor|major
    // TODO support validating against package.json to avoid semver gaps
    let version = matches.value_of("version").unwrap();
    let project = matches.value_of("project").unwrap_or_default();
    if project == "" {
        return Err(format!("empty project not implemented yet"));
    }
    let dry_run = matches.is_present("dry-run");
    let ignore_pending_changes = matches.is_present("ignore-pending-changes");
    ensure_on_default_branch(&matches)?;
    if !ignore_pending_changes {
        ensure_no_pending_changes()?;
    }
    git_fetch()?;
    git_pull()?;
    npm_version(version)?;
    git_cliff(&matches)?;
    git_commit(&matches)?;
    git_tag(&matches)?;
    git_push()?;
    // TODO support snapshot / development version
    // npm_version(version)?; set snapshot version
    //git_commit(version)?;
    //git_push()?;
    Ok(())
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

fn npm_version(version: &str) -> Result<String, String> {
    // npm version --no-git-tag-version patch
    // TODO support non-Windows
    Command::new("cmd")
        .arg("/C")
        .arg("npm.cmd")
        .arg("version")
        .arg("--no-git-tag-version")
        .arg(version)
        .run()
}

fn git_cliff(matches: &ArgMatches) -> Result<String, String> {
    // git-cliff --include-path 'generator-python-kamino/*' -r .. -o CHANGELOG.md -t 0.0.1
    let include_path = matches
        .value_of("git-cliff-include-path")
        .unwrap_or_default();
    let repository = matches.value_of("repository").unwrap_or_default();
    let version = matches.value_of("version").unwrap_or_default();
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

fn git_commit(matches: &ArgMatches) -> Result<String, String> {
    let version = matches.value_of("version").unwrap_or_default();
    let project = matches.value_of("project").unwrap_or_default();
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

fn git_tag(matches: &ArgMatches) -> Result<String, String> {
    // TODO if possible add the changelog of only the current release in the git tag
    let version = matches.value_of("version").unwrap_or_default();
    let project = matches.value_of("project").unwrap_or_default();
    let tag_style = matches.value_of("git-tag-style").unwrap_or_default();
    let msg = if project != "" {
        format!("Releasing version {} of {}", version, project)
    } else {
        format!("Releasing version {}", version)
    };
    let tag = if tag_style == "project-slash-version" && project != "" {
        format!("{}/{}", project, version)
    } else if tag_style == "v-version" {
        format!("v{}", version)
    } else {
        format!("{}", version)
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
