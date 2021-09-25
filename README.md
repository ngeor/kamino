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
- Cleanup with `mvn release:clean`

### Automatic release (via GitHub Actions)

Prepare the release with `mvn release:prepare`.

Other options:

- Without prompting for each version: `mvn -B release:prepare`
- Dry run mode: `mvn -DdryRun=true release:prepare`

### Manual release

Run `./scripts/deploy.sh` (or `./scripts/deploy-local.sh` if it exists).
