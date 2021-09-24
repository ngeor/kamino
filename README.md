# java
Open source Java code

![Build status](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)

## Repository structure

- apps: Standalone applications (Desktop and Web)
- libs: Libraries
- maven-archetypes: Maven Archetypes
- maven-plugins: Maven Plugins
- scripts: Small scripts and tooling for the repo itself and CI

## Releasing

- Make sure you're on the default branch and there are no pending changes
- Make sure `CHANGELOG.md` is up to date before releasing
- Run `./scripts/deploy.sh` (or `./scripts/deploy-local.sh` if it exists)
