extern crate clap;
use clap::{Arg, ArgMatches, Command};

const VERSION: &str = "version";
const SNAPSHOT_VERSION: &str = "snapshot-version";
const TYPE: &str = "type";
const IGNORE_PENDING_CHANGES: &str = "ignore-pending-changes";
const GIT_REMOTE_NAME: &str = "git-remote-name";
const NO_PUSH: &str = "no-push";

const PROJECT_TYPE_NPM: &str = "npm";
const PROJECT_TYPE_PIP: &str = "pip";

pub trait Args {
    fn version(&self) -> &str;

    fn snapshot_version(&self) -> &str;

    fn project_type(&self) -> Option<ProjectType>;

    fn ignore_pending_changes(&self) -> bool;

    fn git_remote_name(&self) -> &str;

    fn push(&self) -> bool;
}

impl Args for ArgMatches {
    fn version(&self) -> &str {
        self.value_of(VERSION).unwrap_or_default()
    }

    fn snapshot_version(&self) -> &str {
        self.value_of(SNAPSHOT_VERSION).unwrap_or_default()
    }

    fn project_type(&self) -> Option<ProjectType> {
        match self.value_of(TYPE) {
            Some(PROJECT_TYPE_NPM) => Some(ProjectType::NPM),
            Some(PROJECT_TYPE_PIP) => Some(ProjectType::PIP),
            _ => None,
        }
    }

    fn ignore_pending_changes(&self) -> bool {
        self.is_present(IGNORE_PENDING_CHANGES)
    }

    fn git_remote_name(&self) -> &str {
        self.value_of(GIT_REMOTE_NAME).unwrap_or_default()
    }

    fn push(&self) -> bool {
        !self.is_present(NO_PUSH)
    }
}

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub enum ProjectType {
    NPM,
    PIP,
}

pub fn parse_args() -> ArgMatches {
    app().get_matches()
}

fn app<'a>() -> Command<'a> {
    // TODO: pick up name and version from Cargo.toml
    Command::new("krt")
        .version("0.1.0")
        .author("Nikolaos Georgiou <nikolaos.georgiou@gmail.com>")
        .about("kamino release tool")
        .arg(
            Arg::new(VERSION)
                .help("Specify the target version")
                .takes_value(true)
                .required(true),
        )
        .arg(
            Arg::new(SNAPSHOT_VERSION)
                .short('s')
                .long(SNAPSHOT_VERSION)
                .help("An optional version to use for the next development iteration")
                .takes_value(true)
                .required(false),
        )
        .arg(
            Arg::new(TYPE)
                .short('t')
                .long(TYPE)
                .help("The type of project to release")
                .takes_value(true)
                .required(false)
                .possible_values([PROJECT_TYPE_NPM, PROJECT_TYPE_PIP]),
        )
        .arg(
            Arg::new(IGNORE_PENDING_CHANGES)
                .long(IGNORE_PENDING_CHANGES)
                .help("Do not check for any pending changes")
                .required(false),
        )
        .arg(
            Arg::new(GIT_REMOTE_NAME)
                .long(GIT_REMOTE_NAME)
                .help("The name of the git remote")
                .takes_value(true)
                .required(false)
                .default_value("origin"),
        )
        .arg(
            Arg::new(NO_PUSH)
                .long(NO_PUSH)
                .help("Do not push to the git remote")
                .required(false),
        )
}

#[cfg(test)]
mod tests {
    use super::*;

    fn parse_args_from(args: Vec<&str>) -> clap::Result<ArgMatches> {
        app().try_get_matches_from(args)
    }

    #[test]
    fn test_parse_empty_args() {
        parse_args_from(vec![""]).expect_err("Should fail parsing empty args");
    }

    #[test]
    fn test_parse_no_args() {
        parse_args_from(vec!["krt"]).expect_err("Should fail parsing no args");
    }

    #[test]
    fn test_parse_version_only() {
        let args = parse_args_from(vec!["krt", "0.1.0"]).unwrap();
        assert_eq!(args.value_of("version").unwrap_or_default(), "0.1.0");
    }

    #[test]
    fn test_parse_version_with_args() {
        let args = parse_args_from(vec!["krt", "0.2.0"]).unwrap();
        assert_eq!(args.version(), "0.2.0");
        assert!(!args.ignore_pending_changes());
        assert_eq!(args.git_remote_name(), "origin");
        assert_eq!(args.project_type(), None);
        assert!(args.push());
    }

    #[test]
    fn test_parse_no_push() {
        let args = parse_args_from(vec!["krt", "0.2.0", "--no-push"]).unwrap();
        assert_eq!(args.version(), "0.2.0");
        assert!(!args.push());
    }
}
