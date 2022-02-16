use std::process::Command;

fn main() -> Result<(), String> {
    ensure_on_default_branch()?;
    Ok(())
}

pub trait EasyCommand {
    fn do_it(&mut self) -> Result<String, String>;
}

impl EasyCommand for Command {
    fn do_it(&mut self) -> Result<String, String> {
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

fn ensure_on_default_branch() -> Result<(), String> {
    let default_branch = git_default_branch()?;
    let current_branch = git_current_branch()?;
    if current_branch == default_branch {
        Ok(())
    } else {
        Err("Not on the default branch".to_owned())
    }
}

fn git_default_branch() -> Result<String, String> {
    // git symbolic-ref refs/remotes/origin/HEAD
    // refs/remotes/origin/trunk
    let output = Command::new("git")
        .arg("symbolic-ref")
        .arg("refs/remotes/origin/HEAD")
        .do_it()?;
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
        .do_it()
}

#[derive(Debug)]
pub enum CmdError {}
