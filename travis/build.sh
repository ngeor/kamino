#!/bin/bash
set -e # break on error

# Import GPG key
gpg --passphrase=${GPG_PASSPHRASE} --no-use-agent --output - keys.asc | gpg --import

# install archetype locally
mvn -B -s settings.xml -P gpg clean install

# get version from pom
VERSION=$(cat pom.xml | grep '<version>' -m 1 | sed 's/[^0-9.]//g')
echo "Creating dummy app based on version ${VERSION}"
# create a dummy app based on it
cd target
mvn archetype:generate -DgroupId=myapp \
    -DartifactId=myapp \
    -DarchetypeGroupId=com.github.ngeor \
    -DarchetypeArtifactId=archetype-quickstart-jdk8 \
    -DarchetypeVersion=${VERSION} \
    -DinteractiveMode=false \
    -DarchetypeCatalog=local

# test the dummy app
cd myapp
mvn test

# cleanup GPG keys
gpg --fingerprint --with-colons ${GPG_KEY} |\
    grep "^fpr" |\
    sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p' |\
    xargs gpg --batch --delete-secret-keys

gpg --batch --yes --delete-key ${GPG_KEY}
