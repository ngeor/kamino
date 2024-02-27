# Changelog

## Unreleased

### Features

* Added yak4j-git library
* Added support for preparing library releases
* Implemented rev-list retrieval
* Added getMostRecentTagWithDate
* Added resetOne and deleteTag methods
* Support `--tags` and `--follow-tags`
* Introducing `Tag` type to disambiguate between tags and versions
* Added methods getCurrentBranch, checkoutNewBranch, ensureOnDefaultBranch, clone, symbolicRef
* Added methods hasNonStagedChanges, lsFiles
* Added fetch method
* Added methods initAndConfigureIdentity, configureIdentity

### Fixes

* Upgrading libraries to Java 17
* Remove quotes from format argument
* Support tags at HEAD

### Miscellaneous Tasks

* Use `com.github.ngeor:java` as the parent pom everywhere
* Added missing javadoc comment
* Switch to snapshot version of parent pom
* Use collection-based overloads
* Use dependency management for commons-io, commons-lang3, jsr305 from parent pom
* Use TempDir annotation in tests
* Moved code to `git` sub-package
* Moved code to `process` sub-package
* feat(yak4j-git): Specify initial branch in git init

### Dependencies

* Upgraded parent pom
