#!/usr/bin/env -S python -u

import argparse
import os
import os.path
import subprocess


def main():
    args = parse_args()

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
    finally:
        clean_gpg(args.gpg_key)


def parse_args():
    parser = argparse.ArgumentParser(description="Creates a release of a Maven library")
    parser.add_argument("--gpg-key", help="GPG key", required=True)
    parser.add_argument("--gpg-passphrase", help="GPG passphrase", required=True)
    parser.add_argument("--gpg-key-file", help="GPG key file", required=True)
    parser.add_argument("--git-user-name", help="Configure the git user.name property")
    parser.add_argument("--git-user-email", help="Configure the git user.email property")
    parser.add_argument("--version-from-github-actions", help="Derive the release version from the current branch as specified from GitHub Actions environment variables", action="store_true")
    return parser.parse_args()
#     prepare_release()
#     push_changes()
#     try:
#         import_gpg()
#         perform_release()
#     except:
#         clean_gpg()


def configure_git_identity(user_name, user_email):
    if not user_name and not user_email:
        return
    if not user_name or not user_email:
        raise ValueError("To configure git identity, both user name and email must be specified")
    subprocess.run([
        "git", "config", "user.name", user_name
    ], check=True)
    subprocess.run([
        "git", "config", "user.email", user_email
    ], check=True)


def prepare_release(release_version):
    subprocess.run([
        "mvn",
        "-B",
        "-DpushChanges=false",
        f"-DreleaseVersion={release_version}",
        "release:prepare"
    ], check=True)


# def push_changes():
#     subprocess.run([
#         "git", "push", "--follow-tags"
#     ], check=True)


def import_gpg(gpg_passphrase, gpg_key_file):
    # gpg --batch --yes --passphrase=${GPG_PASSPHRASE} --output - $KEYS_ASC | gpg --batch --yes --import
    with subprocess.Popen([
        "gpg",
        "--batch",
        "--yes",
        f"--passphrase={gpg_passphrase}",
        "--output",
        "-",
        gpg_key_file
    ], stdout=subprocess.PIPE) as p1:
        with subprocess.Popen([
            "gpg", "--batch", "--yes", "--import"
        ], stdin=p1.stdout):
            pass


def clean_gpg(gpg_key):
    # gpg -K | grep "^  " | tr -d " " | xargs gpg --batch --yes --delete-secret-keys
    # gpg --batch --yes --delete-key ${GPG_KEY}
    p1 = subprocess.run(["gpg", "-K"], check=True, encoding="utf8", stdout=subprocess.PIPE)
    keys = p1.stdout.splitlines()
    for key in keys:
        if key.startswith("  "):
            subprocess.run(["gpg", "--batch", "--yes", "--delete-secret-keys", key.strip()], check=True)
    subprocess.run(["gpg", "--batch", "--yes", "--delete-key", gpg_key], check=True)


# def perform_release():
#     subprocess.run([
#         "mvn",
#         "-B",
#         "-DlocalCheckout=true",
#         "-DreleaseProfiles=gpg",
#         "release:perform"
#     ], check=True)


def get_version_from_github():
    ref_name = os.environ["GITHUB_REF_NAME"]
    ref_type = os.environ["GITHUB_REF_TYPE"]
    if ref_type != "branch":
        raise ValueError(f"GITHUB_REF_TYPE was {ref_type} and not 'branch'")
    parts = ref_name.split("-", maxsplit=1)
    if len(parts) != 2 or not parts[1]:
        raise ValueError(f"Branch {ref_name} is not in the expected format 'release-x.y.z'")
    return parts[1]


if __name__ == "__main__":
    main()
