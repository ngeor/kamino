# Changelog

## Unreleased

### Features

* Integrated version calculator in release tooling
* Added updateVersion method
* Add dry run and no push support
* Generating the release over the effective pom
* Use new effective pom resolution
* Improved effective pom resolution
* Add method `TagPrefix#tag`
* Introducing `Tag` type to disambiguate between tags and versions
* Use maven versions plugin instead of maven release plugin for the release
* Adding sanity checks about the `pom.xml`
* Implement sanity checks before attempting release
* Added sanity checks against the Maven pom file before preparing the release
* Added git sanity checks
* Make XML indentation configurable
* Use annotated tags for releases

### Fixes

* Use a temporary file for backing up pom.xml
* Merge plugin configurations
* Push tags upon releasing
* Fix setting the next snapshot version
* Accidentally kept snapshot version in release pom

### Miscellaneous Tasks

* Switch to snapshot version of parent pom
* propagate ProcessFailedException
* Rename yak4j-semver to versions
* Moved versions into its own package
* Moved code to `mr` package
* Converted TagPrefix into a non-static class
* Moved code to `maven` package
* Encapsulation of XML document wrapper inside `MavenDocument`
* Moved effective pom logic into MavenDocument
* Use dependency management for commons-io, commons-lang3, jsr305 from parent pom
* Move validation tests to nested class
* Use TempDir annotation in tests
* Use methods initAndConfigureIdentity, configureIdentity
* Moved code to `git` sub-package
* Moved code to `process` sub-package
* Removed MavenModule in favor of PomRepository
* Arranging code in packages
* Adding tests
* Moved inner classes to upper level
* Make FileInput convert file to the canonical format
* feat(yak4j-git): Specify initial branch in git init
* Moved inner classes up
* Extracted CommitGrouper class

### Dependencies

* Upgraded parent pom
