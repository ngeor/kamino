# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/ngeor/java/compare/v2.0.0...trunk)

## [2.0.0](https://github.com/ngeor/java/compare/v1.10.0...v2.0.0) - 2021-10-03

- Moved away from monorepo approach, back to multirepo. Each project has moved
  to its own separate repository. This project is now just the parent pom.
- Known issue: checkstyle has been disabled  

## [1.10.0](https://github.com/ngeor/java/compare/v1.9.0...v1.10.0) - 2021-09-28

- `yak4j-cli` 0.10.0
  - Building an uber jar with maven-shade-plugin
  - Wrapper script runs jar file from temp folder
- Improve gpg cleanup during release workflow

## [1.9.0](https://github.com/ngeor/java/compare/v1.8.0...v1.9.0) - 2021-09-26

- `yak4j-cli` 0.9.0
  - Auto-generate the launcher shell script
  - Adding release command
- `yak4j-dom` 1.8.0
  - New method `ElementWrapper#removeChildNodes`

## [1.8.0](https://github.com/ngeor/java/compare/v1.7.0...v1.8.0) - 2021-09-25

### Changed

- `yak4j-spring-test-utils` Bump spring from 5.3.8 to 5.3.10
- Fixed Changelog links
- Updating readme with instructions on how to release a single library

### Removed

- Removed Travis and Coveralls badges, removed old child repo build scripts

## [1.7.0](https://github.com/ngeor/java/compare/v1.4.0...v1.7.0) - 2021-09-25

### Changed

- Using GitHub Actions instead of Travis for build and release

## [1.4.0](https://github.com/ngeor/java/compare/v1.3.0...v1.4.0) - 2021-07-04

### Changed

- `archetype-quickstart-jdk8`
    - Only updating archetype resource version numbers for release versions
- `yak4j-bitbucket-maven-plugin`
    - Upgraded Jackson to 2.12.3 (aligned with parent pom)
    - Upgraded okhttp to 4.9.1
- `yak4j-spring-test-utils`
    - Upgraded Spring version to 5.3.8

### Removed

- `yak4j-cli`
    - Removed the bump functionality, as the maven release plugin suffices

## [1.3.0](https://github.com/ngeor/java/compare/v1.2.0...v1.3.0) - 2021-07-03

### Changed

- Adjusting maven release plugin

## [1.2.0](https://github.com/ngeor/java/compare/v1.1.0...v1.2.0) - 2021-07-03

### Added

- Using maven release plugin

## [1.1.0](https://github.com/ngeor/java/compare/v1.0.0...v1.1.0) - 2021-07-02

### Added

- Added changelog

### Changed

- Changed license to MIT
- Added more enforcer rules
- Setting `maven-archetype-plugin` version to 3.2.0
- `archetype-quickstart-jdk8`
    - Removed travis and coveralls support
    - Specifying `maven-archetype` packaging
    - Adding archetype tests
    - Using `package` property for the generated package instead of `groupId`
- `bprr`
    - Upgraded to Spring Boot 2.5.2

## 1.0.0 - 2021-06-27

- First release in the new Java repo
