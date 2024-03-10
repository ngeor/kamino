# Changelog

## [1.0.0] - 2024-03-10

### Features

* Added yak4j-process library
* Offer overload methods that accept arguments as collection
* Only returning ProcessFailedException
* Include process output to the exception
* Added parent pom for public libraries

### Fixes

* Upgrading libraries to Java 17
* Deadlock waiting for process
* process was blocking when stderr had content

### Miscellaneous Tasks

* Use `com.github.ngeor:java` as the parent pom everywhere
* Switch to snapshot version of parent pom
* Moved code to `process` sub-package
* Offer subclasses access to the process builder
* Added name and description to pom.xml

### Dependencies

* Upgraded parent pom
