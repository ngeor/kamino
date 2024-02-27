# Changelog

## Unreleased

### Features

* Added yak4j-maven library
* Resolve properties in custom effective pom resolver
* Resolve parent pom in custom effective pom resolver
* Switch to custom effective pom calculator
* Support relative path poms
* Add dry run and no push support
* Exclude pom.xml from modification checks
* Support merging build plugins
* Improved effective pom resolution
* Improved effective pom resolution
* Introducing `Tag` type to disambiguate between tags and versions
* Use maven versions plugin instead of maven release plugin for the release
* Added modelVersion, name, description methods
* Implemented basic parent resolution in `PomRepository`
* Adding Input and Resolver abstractions
* Add deploy method, add support for custom settings file

### Fixes

* Merge child into parent
* Support plugin executions
* Merge plugin configurations
* Merge plugin configurations
* Fix setting the next snapshot version

### Miscellaneous Tasks

* Adding some tests to yak4j-maven
* Maven#effectivePom returns a DocumentWrapper
* Use `com.github.ngeor:java` as the parent pom everywhere
* Switch to snapshot version of parent pom
* propagate ProcessFailedException
* Add tests for merging pom documents
* Move some logic into ParentPom record
* Return document from `PomMerger.merge`
* Builder-style API in PomMerger to avoid mixing parent and child parameters
* Use collection-based overloads
* Using assertJ's isEqualToNormalizingNewlines in the tests
* Moved code to `maven` package
* Encapsulation of XML document wrapper inside `MavenDocument`
* Moved effective pom logic into MavenDocument
* Using method getTextContentTrimmedAsStream
* Use dependency management for commons-io, commons-lang3, jsr305 from parent pom
* Use TempDir annotation in tests
* Moved code to `process` sub-package
* Adding ModuleFinder to yak4j-maven
* Adding MavenModuleNg
* Added sub-classes of MavenModuleNg
* Deleted old MavenModule
* Use overload `findChildElements` to get elements of multiple names
* Added `PomRepository`
* Adding `ResolutionPhase`
* Removed MavenModule in favor of PomRepository
* Arranging code in packages
* Removed DOM logic from MavenCoordinates
* Removed DOM logic from ParentPom
* Adding tests
* Use new `firstElementsText` method
* Removed `MavenDocument`
* Use `transformTextNodes` method
* Moved inner classes to upper level
* Make FileInput convert file to the canonical format
* Make load method private
* Change isKnown to isUnknown
* Deprecated 3 methods

### Dependencies

* Upgraded parent pom
