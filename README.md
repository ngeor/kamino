# krt

[![Java CI with Maven](https://github.com/ngeor/krt/actions/workflows/maven.yml/badge.svg)](https://github.com/ngeor/krt/actions/workflows/maven.yml)

> Kamino Release Tool

krt is a Java CLI application that can be used to release libraries.
In short, it bumps the version of the library, updates its changelog,
pushes a tag, and bumps again to a snapshot version.

## Workflow

krt will first perform the following checks:

- ensure you're on the default branch
- ensure no pending changes exist
- ensure you're on the latest and greatest
- allow specifiying the target version with the identifiers "major", "minor", "patch"
- ensures there are no gaps in the semver sequence
- ensures there are no duplicate tags

Then, it will bump the version:

- set the target version
- generate the changelog with [git-cliff](https://github.com/orhun/git-cliff)
- create a commit, tag it, push it

Finally, it will switch the version to a snapshot version and push it again.

## Supported tech stacks

- Maven
- NPM (requires `npm` to be installed)
- Python/Pip (expects a `setup.cfg` file)

## Download

In the [releases page](https://github.com/ngeor/kamino/releases?q=krt&expanded=true) you'll find
pre-built native binaries that don't require Java.
