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

## Usage

The artifactId is `archetype-quickstart-jdk8`
and the groupId is `com.github.ngeor`.

To create a new app based on it:

```
mvn archetype:generate -DgroupId=com.mycompany.myapp \
    -DartifactId=myapp \
    -DarchetypeGroupId=com.github.ngeor \
    -DarchetypeArtifactId=archetype-quickstart-jdk8 \
    -DarchetypeVersion=1.2.0 \
    -DinteractiveMode=false
```

Tip: double check `1.2.0` is the latest version, in case this README is outdated
(happens to the best of us).

## Contributing

If you want to make changes, you'll need to test the archetype locally.

- Clone the repository
- Use `mvn install` to have the archetype available locally

To test it, generate a dummy app. The command is the same as above, but
pass `-DarchetypeCatalog=local` to make sure it's not using the internet

## Travis CI for archetype

CI is performed via a shell script (`travis/build.sh`).

- GPG keys are decrypted and imported from `keys.asc`. This is needed for signing artifacts before publishing them to the central maven repository. Environment variables: `GPG_KEY` and `GPG_PASSPHRASE`.
- The project is installed locally
- To test the archetype, a small app is generated out of it and the app is tested
- GPG keys are deleted

Deployment happens by tagging. Make sure the `pom.xml` is updated to match the tag!
