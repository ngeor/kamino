use std::process::Command;
extern crate clap;
use clap::{App, Arg, ArgMatches};

fn main() -> Result<(), String> {
    let matches = parse_args();
    let version = matches.value_of("version").unwrap();
    let project = matches.value_of("project").unwrap();
    let dry_run = matches.is_present("dry-run");
    let ignore_pending_changes = matches.is_present("ignore-pending-changes");
    ensure_on_default_branch()?;
    if !ignore_pending_changes {
        ensure_no_pending_changes()?;
    }
    npm_version(version)?;
    git_cliff(project, version)?;
    git_commit(version)?;
    git_tag(project, version)?;
    git_push()?;
    // npm_version(version)?; set snapshot version
    //git_commit(version)?;
    //git_push()?;
    Ok(())
}

fn parse_args() -> ArgMatches {
    // TODO: pick up name and version from Cargo.toml
    App::new("krt")
        .version("0.1.0")
        .author("Nikolaos Georgiou <nikolaos.georgiou@gmail.com>")
        .about("kamino release tool")
        .arg(
            Arg::new("project")
                .short('p')
                .long("project")
                .help("The project to release in a monorepo")
                .takes_value(true)
                .required(true),
        )
        .arg(
            Arg::new("version")
                .short('v')
                .help("Specify the target version")
                .takes_value(true)
                .required(true),
        )
        .arg(
            Arg::new("dry-run")
                .long("dry-run")
                .help("Do not actually modify anything")
                .required(false),
        )
        .arg(
            Arg::new("ignore-pending-changes")
                .long("ignore-pending-changes")
                .help("Do not check for any pending changes")
                .required(false),
        )
        .get_matches()
}

fn ensure_on_default_branch() -> Result<(), String> {
    let default_branch = git_default_branch()?;
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

fn git_default_branch() -> Result<String, String> {
    // git symbolic-ref refs/remotes/origin/HEAD
    // refs/remotes/origin/trunk
    let output = Command::new("git")
        .arg("symbolic-ref")
        .arg("refs/remotes/origin/HEAD")
        .run()?;
    let (_, second) = output.split_at("refs/remotes/origin/".len());
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
    Command::new("cmd")
        .arg("/C")
        .arg("npm.cmd")
        .arg("version")
        .arg("--no-git-tag-version")
        .arg(version)
        .run()
}

fn git_cliff(project: &str, version: &str) -> Result<String, String> {
    // git-cliff --include-path 'generator-python-kamino/*' -r .. -o CHANGELOG.md -t 0.0.1
    let include_path = format!("{}/*", project);
    Command::new("git-cliff")
        .arg("--include-path")
        .arg(include_path)
        .arg("-r")
        .arg("..") // TODO fix this
        .arg("-o")
        .arg("CHANGELOG.md")
        .arg("-t")
        .arg(version)
        .run()
}

fn git_commit(version: &str) -> Result<String, String> {
    let msg = format!("chore(release): prepare for {}", version);
    Command::new("git")
        .arg("commit")
        .arg("-a") // TODO only commit files we know are supposed to have changed
        .arg("-m")
        .arg(msg)
        .run()
}

fn git_tag(project: &str, version: &str) -> Result<String, String> {
    // TODO if possible add the changelog of only the current release in the git tag
    let msg = format!("Releasing version {} of {}", version, project);
    let tag = format!("{}/{}", project, version);
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
