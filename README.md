# java

[![Java CI with Maven](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)](https://github.com/ngeor/java/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/java.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22java%22)

Parent pom for Java projects

![Build status](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)

## Releasing

- Make sure you're on the default branch and there are no pending changes
- Push a release branch in the naming convention `release-x.y.z`
- Wait for GitHub actions to publish the release
- Merge to the main branch and delete the release branch
- Update the change log with `git cliff` and commit with
  a message like `chore(changelog): Updated changelog`
