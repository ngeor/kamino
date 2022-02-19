extern crate clap;
use clap::{App, Arg, ArgMatches};

pub fn parse_args() -> ArgMatches {
    app().get_matches()
}

fn app<'a>() -> App<'a> {
    // TODO: pick up name and version from Cargo.toml
    App::new("krt")
        .version("0.1.0")
        .author("Nikolaos Georgiou <nikolaos.georgiou@gmail.com>")
        .about("kamino release tool")
        .arg(
            Arg::new("version")
                .help("Specify the target version")
                .takes_value(true)
                .required(true),
        )
        .arg(
            Arg::new("type")
                .short('t')
                .long("type")
                .help("The type of project to release")
                .takes_value(true)
                .required(false)
                .possible_values(["npm"]),
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
        .arg(
            Arg::new("git-remote-name")
                .long("git-remote-name")
                .help("The name of the git remote")
                .takes_value(true)
                .required(false)
                .default_value("origin"),
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
        let matches = parse_args_from(vec!["krt", "0.1.0"]).unwrap();
        assert_eq!(matches.value_of("version").unwrap_or_default(), "0.1.0");
    }
}
