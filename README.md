# java

Parent pom for Java projects

![Build status](https://github.com/ngeor/java/actions/workflows/maven.yml/badge.svg)

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

