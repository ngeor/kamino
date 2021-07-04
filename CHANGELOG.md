# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

- `archetype-quickstart-jdk8`
    - Only updating archetype resource version numbers for release versions

### Removed

- `yak4j-cli`
    - Removed the bump functionality, as the maven release plugin suffices

## [1.3.0](https://github.com/ngeor/java/compare/v1.3.0...trunk) - 2021-07-03

### Changed

- Adjusting maven release plugin

## [1.2.0](https://github.com/ngeor/java/compare/v1.2.0...v1.3.0) - 2021-07-03

### Added

- Using maven release plugin

## [1.1.0](https://github.com/ngeor/java/compare/v1.1.0...v1.2.0) - 2021-07-02

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

## [1.0.0](https://github.com/ngeor/java/compare/v1.0.0...v1.1.0) - 2021-06-27

- First release in the new Java repo
