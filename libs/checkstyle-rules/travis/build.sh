#!/bin/bash
set -e # break on error

# Import GPG key
gpg --passphrase=${GPG_PASSPHRASE} --no-use-agent --output - ./travis/keys.asc | gpg --import

# Run maven goal
mvn -B -s ./travis/settings.xml -P gpg clean $1

# cleanup GPG keys
gpg --fingerprint --with-colons ${GPG_KEY} |\
  grep "^fpr" |\
  sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p' |\
  xargs gpg --batch --delete-secret-keys

gpg --batch --yes --delete-key ${GPG_KEY}
