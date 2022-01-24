# java

[![Java CI with Maven](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)](https://github.com/ngeor/java/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/java.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22java%22)

Parent pom for Java projects

![Build status](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)

## Releasing

- Make sure you're on the default branch and there are no pending changes
- Make sure `CHANGELOG.md` is up to date before releasing
- Cleanup with `mvn release:clean`

### Automatic release (via GitHub Actions)

Push the `release` branch and wait for GitHub Actions to publish evreything.
Merge the `release` branch into `trunk` once complete.

### Manual release

Run `./scripts/deploy.sh` (or `./scripts/deploy-local.sh` if it exists).
