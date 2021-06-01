#!/bin/bash

# Import GPG key
gpg --passphrase=${GPG_PASSPHRASE} --no-use-agent --output - keys.asc | gpg --import

echo "publishing to maven"
mvn -B -s settings.xml -P gpg deploy

# cleanup
gpg --fingerprint --with-colons ${GPG_KEY} |\
    grep "^fpr" |\
    sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p' |\
    xargs gpg --batch --delete-secret-keys

gpg --batch --yes --delete-key ${GPG_KEY}
