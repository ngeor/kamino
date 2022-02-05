# java

[![Java CI with Maven](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)](https://github.com/ngeor/java/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/java.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22java%22)

Parent pom for Java projects

## Releasing

- Make sure you're on the default branch and there are no pending changes
- Push a release branch in the naming convention `release-x.y.z`
  - Tip: do that with `./scripts/release.py initialize --version x.y.z`
- Wait for GitHub actions to publish the release
- Merge to the main branch and delete the release branch
  - Tip: do that with `./scripts/release.py finalize`
