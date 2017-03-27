# archetype-quickstart-jdk8

[![Build Status](https://travis-ci.org/ngeor/archetype-quickstart-jdk8.svg?branch=master)](https://travis-ci.org/ngeor/archetype-quickstart-jdk8)

A Maven archetype for a simple Java app, updated for Java 8.

This is effectively the same as the maven-archetype-quickstart, with the following changes:

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

The artifactId is `archetype-quickstart-jdk8` and the groupId is `ngeor.archetypes`.

To create a new app based on it:

```
mvn archetype:generate -DgroupId=com.mycompany.myapp \
    -DartifactId=myapp \
    -DarchetypeGroupId=ngeor.archetypes \
    -DarchetypeArtifactId=archetype-quickstart-jdk8 \
    -DarchetypeVersion=1.0.0 \
    -DinteractiveMode=false
```

Pro tip: You can also pass `-DarchetypeCatalog=local` to skip checking the network.

## Uninstall

Delete the folder `ngeor` from the `~/.m2` local repository, e.g.

```
rm -rf ~/.m2/repository/ngeor/
```
