use crate::{ProjectType, SimpleCommand};
use std::fs;
use std::process::Command;

pub fn set_version(project_type: ProjectType, version: &str) -> Result<(), String> {
    match project_type {
        ProjectType::NPM => {
            // bump version in package.json and package-lock.json via external command
            npm_version(version).map(|_| ())
        }
        ProjectType::PIP => pip_version(version).map_err(|e| e.to_string()),
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
fn pip_version(version: &str) -> Result<(), PipError> {
    let setup_cfg_contents = String::from_utf8(fs::read("setup.cfg")?)?;
    let module_name = pip_get_module_name_from_setup_cfg(&setup_cfg_contents)?;
    let module_file = format!("{}/__init__.py", module_name);
    let module_contents = String::from_utf8(fs::read(&module_file)?)?;
    let new_contents = pip_update_version(&module_contents, version);
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
