# java
Open source Java code

![Build status](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)

## Repository structure

- apps: Standalone applications (Desktop and Web)
- libs: Libraries
- maven-archetypes: Maven Archetypes
- maven-plugins: Maven Plugins
- scripts: Small scripts and tooling for the repo itself and CI

## Building

- Build everything with `mvn package`
- Build just one package with `mvn package -pl PACKAGE_DIR -am`

## Releasing

### Releasing everything

Follow this section if you want to release every artifact in the repository
(e.g. if you bumped some version in the parent pom).

- Make sure you're on the default branch and there are no pending changes
- Make sure `CHANGELOG.md` is up to date before releasing
- Cleanup with `mvn release:clean`

#### Automatic release (via GitHub Actions)

Prepare the release with `mvn release:prepare`.

Other options:

- Without prompting for each version: `mvn -B release:prepare`
- Dry run mode: `mvn -DdryRun=true release:prepare`

#### Manual release

Run `./scripts/deploy.sh` (or `./scripts/deploy-local.sh` if it exists).

### Releasing a single library

Inspired by:

- [Open Source Libs - Maven - Monorepo](https://opensourcelibs.com/lib/logiball-monorepo)
- [Maven In A Google Style Monorepo](https://paulhammant.com/2017/01/27/maven-in-a-google-style-monorepo/)

Provided by `yak4j-cli` with the release sub-command,
run `./scripts/yak4j-cli.sh release module [...module]`

Steps that are run under the hood

- Create a branch
- Remove the modules that aren't supposed to be released from the parent `pom.xml`, commit
- Prepare the release with `mvn release:prepare`. Only the parent pom and the non-deleted modules should be released.
- Revert the commented out modules
- Update the parent pom version in all the child modules
- Merge and delete the branch
