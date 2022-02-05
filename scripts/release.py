#!/usr/bin/env -S python -u

import argparse
import os
import os.path
import subprocess
import tempfile


def main():
    args = parse_args()
    if args.subparser_name == "initialize":
        initialize(args)
    elif args.subparser_name == "release":
        release(args)
    elif args.subparser_name == "finalize":
        finalize(args)
    else:
        raise ValueError(f"Unsupported command: {args.subparser_name}")


def parse_args():
    parser = argparse.ArgumentParser(description="Creates a release of a Maven library")
    subparsers = parser.add_subparsers(
        dest="subparser_name", help="Sub-commands help", required=True
    )
    initialize_parser = subparsers.add_parser(
        "initialize", help="Initializes the release"
    )
    initialize_parser.add_argument(
        "--version", help="The version to release, in x.y.z format", required=True
    )
    release_parser = subparsers.add_parser("release", help="Performs the release")
    release_parser.add_argument("--gpg-key", help="GPG key", required=True)
    release_parser.add_argument(
        "--gpg-passphrase", help="GPG passphrase", required=True
    )
    release_parser.add_argument("--gpg-key-file", help="GPG key file", required=True)
    release_parser.add_argument(
        "--git-user-name", help="Configure the git user.name property"
    )
    release_parser.add_argument(
        "--git-user-email", help="Configure the git user.email property"
    )
    release_parser.add_argument(
        "--version-from-github-actions",
        help="Derive the release version from the current branch as specified from GitHub Actions environment variables",
        action="store_true",
    )
    release_parser.add_argument(
        "--maven-username",
        help="The username to use for publishing the release to Maven",
        required=True,
    )
    release_parser.add_argument(
        "--maven-password",
        help="The password to use for publishing the release to Maven",
        required=True,
    )
    finalize_parser = subparsers.add_parser("finalize", help="Finalizes the release")
    return parser.parse_args()


def initialize(args):
    git = Git()
    # ensure we're on the default branch
    default_branch = git.get_default_branch()
    current_branch = git.get_current_branch()
    if current_branch != default_branch:
        raise ValueError("Please switch to the default branch")
    # ensure there are no pending changes
    if git.has_pending_changes():
        raise ValueError("There are pending changes")
    # create branch based on input version (accept major / minor / patch arguments)
    branch_name = f"release-{args.version}"
    git.create_branch(branch_name)
    # push branch
    git.push_new_branch()


def release(args):
    if args.version_from_github_actions:
        version = get_version_from_github()
    else:
        raise ValueError("Not implemented")
    print(f"Will use version {version}")

    if not os.path.isfile(args.gpg_key_file):
        raise ValueError(f"File not found: {args.gpg_key_file}")

    try:
        import_gpg(args.gpg_passphrase, args.gpg_key_file)
        configure_git_identity(args.git_user_name, args.git_user_email)
        prepare_release(version)
        update_changelog(version)
        push_changes()
        perform_release(
            args.gpg_key, args.gpg_passphrase, args.maven_username, args.maven_password
        )
    finally:
        clean_gpg(args.gpg_key)


def finalize(args):
    git = Git()
    # ensure we're on the release branch
    default_branch = git.get_default_branch()
    current_branch = git.get_current_branch()
    if current_branch == default_branch:
        raise ValueError("Please switch to the release branch")
    # ensure there are no pending changes
    if git.has_pending_changes():
        raise ValueError("There are pending changes")
    # get tag and upstream commits by maven release plugin
    # git fetch -p -t && git pull
    git.fetch()
    git.pull()
    # git checkout default branch
    git.checkout(default_branch)
    # git merge release-x.y.z
    git.merge(current_branch)
    # git branch -D release-x.y.z
    git.delete_local_branch(current_branch)
    # git push origin :release-x.y.z
    git.delete_remote_branch(current_branch)
    # git push
    git.push()


def configure_git_identity(user_name, user_email):
    if not user_name and not user_email:
        return
    if not user_name or not user_email:
        raise ValueError(
            "To configure git identity, both user name and email must be specified"
        )
    subprocess.run(["git", "config", "user.name", user_name], check=True)
    subprocess.run(["git", "config", "user.email", user_email], check=True)


def prepare_release(release_version):
    subprocess.run(
        [
            "mvn",
            "-ntp",
            "-B",
            "-DpushChanges=false",
            f"-DreleaseVersion={release_version}",
            "release:prepare",
        ],
        check=True,
    )


def update_changelog(release_version):
    subprocess.run(["git-cliff", "-o", "CHANGELOG.md"], check=True)
    subprocess.run(["git", "add", "CHANGELOG.md"], check=True)
    subprocess.run(
        [
            "git",
            "commit",
            "-m",
            f"chore(changelog): Update changelog for {release_version}",
        ]
    )


def push_changes():
    subprocess.run(["git", "push", "--follow-tags"], check=True)


def import_gpg(gpg_passphrase, gpg_key_file):
    # gpg --batch --yes --passphrase=${GPG_PASSPHRASE} --output - $KEYS_ASC | gpg --batch --yes --import
    with subprocess.Popen(
        [
            "gpg",
            "--batch",
            "--yes",
            f"--passphrase={gpg_passphrase}",
            "--output",
            "-",
            gpg_key_file,
        ],
        stdout=subprocess.PIPE,
    ) as p1:
        with subprocess.Popen(["gpg", "--batch", "--yes", "--import"], stdin=p1.stdout):
            pass


def clean_gpg(gpg_key):
    # gpg -K | grep "^  " | tr -d " " | xargs gpg --batch --yes --delete-secret-keys
    # gpg --batch --yes --delete-key ${GPG_KEY}
    p1 = subprocess.run(
        ["gpg", "-K"], check=True, encoding="utf8", stdout=subprocess.PIPE
    )
    keys = p1.stdout.splitlines()
    for key in keys:
        if key.startswith("  "):
            subprocess.run(
                ["gpg", "--batch", "--yes", "--delete-secret-keys", key.strip()],
                check=True,
            )
    subprocess.run(["gpg", "--batch", "--yes", "--delete-key", gpg_key], check=True)


def get_version_from_github():
    ref_name = os.environ["GITHUB_REF_NAME"]
    ref_type = os.environ["GITHUB_REF_TYPE"]
    if ref_type != "branch":
        raise ValueError(f"GITHUB_REF_TYPE was {ref_type} and not 'branch'")
    parts = ref_name.split("-", maxsplit=1)
    if len(parts) != 2 or not parts[1]:
        raise ValueError(
            f"Branch {ref_name} is not in the expected format 'release-x.y.z'"
        )
    return parts[1]


def perform_release(gpg_key, gpg_passphrase, maven_username, maven_password):
    with tempfile.TemporaryDirectory() as tmp_dir:
        settings_xml_file = os.path.join(tmp_dir, "settings.xml")
        with open(settings_xml_file, "w") as f:
            f.write(
                f"""
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>{maven_username}</username>
            <password>{maven_password}</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>gpg</id>
            <properties>
                <gpg.keyname>{gpg_key}</gpg.keyname>
                <gpg.passphrase>{gpg_passphrase}</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
            """
            )
        subprocess.run(
            [
                "mvn",
                "-ntp",
                "-B",
                "-s",
                settings_xml_file,
                "-DlocalCheckout=true",
                "release:perform",
            ],
            check=True,
        )


class Git:
    def get_default_branch(self):
        result = subprocess.run(
            ["git", "symbolic-ref", "refs/remotes/origin/HEAD"],
            check=True,
            encoding="utf-8",
            stdout=subprocess.PIPE,
        )
        return result.stdout.strip().replace("refs/remotes/origin/", "")

    def get_current_branch(self):
        result = subprocess.run(
            ["git", "rev-parse", "--abbrev-ref", "HEAD"],
            check=True,
            encoding="utf-8",
            stdout=subprocess.PIPE,
        )
        return result.stdout.strip()

    def has_pending_changes(self):
        result = subprocess.run(["git", "diff", "--quiet"])
        return result.returncode != 0

    def create_branch(self, name):
        subprocess.run(["git", "checkout", "-b", name], check=True)

    def push(self):
        subprocess.run(["git", "push"], check=True)

    def push_new_branch(self):
        subprocess.run(["git", "push", "-u", "origin", "HEAD"], check=True)

    def fetch(self):
        subprocess.run(["git", "fetch", "-p", "-t"], check=True)

    def pull(self):
        subprocess.run(["git", "pull"], check=True)

    def checkout(self, name):
        subprocess.run(["git", "checkout", name], check=True)

    def merge(self, name):
        subprocess.run(["git", "merge", name], check=True)

    def delete_local_branch(self, name):
        subprocess.run(["git", "branch", "-D", name], check=True)

    def delete_remote_branch(self, name):
        subprocess.run(["git", "push", "origin", ":" + name], check=True)


if __name__ == "__main__":
    main()
