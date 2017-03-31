#!/bin/bash
set -e # break on error
echo "Current branch: ${TRAVIS_BRANCH}"

# Import GPG key
gpg --passphrase=${GPG_PASSPHRASE} --no-use-agent --output - keys.asc | gpg --import

# get existing version from pom
# first run to get the downloading out of the way
mvn -q help:evaluate -Dexpression=project.version
MAJOR_MINOR_VERSION=$(mvn help:evaluate -Dexpression=project.version | grep ^[0-9] | cut -d- -f1 | cut -d. -f1,2)

# define a custom version for the pom
NEW_VERSION=${MAJOR_MINOR_VERSION}.${TRAVIS_BUILD_NUMBER}
echo "Will use version ${NEW_VERSION}"

# replace version in pom
mvn versions:set -DnewVersion=${NEW_VERSION}

# debugging purposes
cat pom.xml

# install archetype locally
mvn -V -B -s settings.xml -P gpg clean install

# create a dummy app based on it
cd target
mvn archetype:generate -DgroupId=myapp \
    -DartifactId=myapp \
    -DarchetypeGroupId=com.github.ngeor \
    -DarchetypeArtifactId=archetype-quickstart-jdk8 \
    -DarchetypeVersion=${NEW_VERSION} \
    -DinteractiveMode=false \
    -DarchetypeCatalog=local

# test the dummy app
cd myapp
mvn test

# publish to nexus
if [ "${TRAVIS_BRANCH}" == "master" ]; then
    echo "publishing to maven"
    cd ../..
    mvn -V -B -s settings.xml -P gpg deploy
fi

gpg --fingerprint --with-colons ${GPG_KEY} |\
    grep "^fpr" |\
    sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p' |\
    xargs gpg --batch --delete-secret-keys

gpg --batch --yes --delete-key ${GPG_KEY}
