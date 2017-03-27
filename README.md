# archetype-quickstart-jdk8

[![Build Status](https://travis-ci.org/ngeor/archetype-quickstart-jdk8.svg?branch=master)](https://travis-ci.org/ngeor/archetype-quickstart-jdk8)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ngeor/archetype-quickstart-jdk8/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ngeor/archetype-quickstart-jdk8)

A Maven archetype for a simple Java app, updated for Java 8.

This is effectively the same as the maven-archetype-quickstart,
with the following changes:

- Java version set to 8
- jUnit updated to latest (4.12)
- fixed indentation and formatting (4 spaces)
- removed typo "rigourous" from `AppTest.java` because it is annoying

## Additional features
In addition to the improvements mentioned above,
the following new features are implemented:

- .gitignore file
- checkstyle rules
- travis support
- checkstyle plugin
- jacoco plugin
- javadoc plugin

## Installation

- Clone the repository
- Use `mvn install` to have the archetype available locally

## Usage

The artifactId is `archetype-quickstart-jdk8`
and the groupId is `com.github.ngeor`.

To create a new app based on it:

```
mvn archetype:generate -DgroupId=com.mycompany.myapp \
    -DartifactId=myapp \
    -DarchetypeGroupId=com.github.ngeor \
    -DarchetypeArtifactId=archetype-quickstart-jdk8 \
    -DarchetypeVersion=1.0-SNAPSHOT \
    -DinteractiveMode=false
```

Pro tip: You can also pass `-DarchetypeCatalog=local` to skip checking the network.

## Travis CD for archetype

CI/CD is performed via a shell script (`travis/build.sh`).

- GPG keys are decrypted and imported from `keys.asc`. This is needed for signing artifacts before publishing them to the central maven repository. Environment variables: `GPG_KEY` and `GPG_PASSPHRASE`.
- The pom.xml's version is set to `1.0.TRAVIS_BUILD_NUMBER`
- The project is installed locally
- To test the archetype, a small app is generated out of it and the app is tested
- If we're on the master branch, we deploy to the central maven repository
- GPG keys are deleted
