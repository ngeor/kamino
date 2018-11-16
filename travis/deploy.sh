#!/bin/bash

# Import GPG key
gpg --passphrase=${GPG_PASSPHRASE} --no-use-agent --output - ./travis/keys.asc | gpg --import

echo "publishing to maven"
mvn -B -s ./travis/settings.xml -P gpg -Dgpg.keyname=${GPG_KEY} -Dgpg.passphrase=${GPG_PASSPHRASE} deploy

# cleanup
gpg --fingerprint --with-colons ${GPG_KEY} |\
    grep "^fpr" |\
    sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p' |\
    xargs gpg --batch --delete-secret-keys

gpg --batch --yes --delete-key ${GPG_KEY}
