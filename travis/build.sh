#!/bin/bash

# define a custom version for the pom
CUSTOM_VERSION=1.0.${TRAVIS_BUILD_NUMBER}

echo "Will use version ${CUSTOM_VERSION}"

# replace version in pom
sed -i -e "s/1.0-SNAPSHOT/${CUSTOM_VERSION}/g" pom.xml

# debugging purposes
cat pom.xml

# install archetype locally
mvn clean install

# create a dummy app based on it
cd target
mvn archetype:generate -DgroupId=myapp \
    -DartifactId=myapp \
    -DarchetypeGroupId=com.github.ngeor \
    -DarchetypeArtifactId=archetype-quickstart-jdk8 \
    -DarchetypeVersion=${CUSTOM_VERSION} \
    -DinteractiveMode=false \
    -DarchetypeCatalog=local

# test the dummy app
cd myapp
mvn test
