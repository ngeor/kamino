# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.9.0] - 2018-11-11

### Added
- New module `yak4j-sync-archetype-maven-plugin`

## [0.8.0] - 2018-11-10

### Added
- New module `yak4j-filename-conventions-maven-plugin`

## [0.7.1] - 2018-10-29

### Changed (Swagger Maven Plugin)
- Support for float types in Swagger files
- Definition prefix works inside lists too
- Creating the output directory if it is missing

## [0.7.0] - 2018-10-28

### Added
- New module `yak4j-swagger-maven-plugin`
- Added dependency management for Jackson 2.9.7
  
### Changed
- Upgraded checkstyle from 8.12 to 8.14
- Upgraded jUnit from 5.2.0 to 5.3.1
- Upgraded jUnit platform from 1.2.0 to 1.3.1
- Upgraded Spring dependencies from 5.0.8 to 5.1.1
- Upgraded assertJ from 3.11.0 to 3.11.1
- Upgraded Mockito from 2.21.0 to 2.23.0
- Upgraded Apache Commons Lang3 from 3.7 to 3.8.1

### Removed
- Removed module `yak4j-aws-dynamodb`
- Removed module `yak4j-mapping-test-utils`
- Removed AWS and Spring Boot dependency management

## [0.6.0] - 2018-10-15

### Added
- New module `yak4j-xml`

## [0.5.1] - 2018-09-01

### Added
- Methods `isCreated`, `isConflict`, `isUnauthorized` on `ResponseEntityAssert`.

### Changed
- Indentation for XML files set to 2 spaces instead of 4.
- Sorted pom files automatically.

### Removed
- Removed `spring-cors-filter` module. Please follow the CORS guides
  [from Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/html/cors.html)
  and [from Spring Web](https://spring.io/blog/2015/06/08/cors-support-in-spring-framework).
- Removed method `hasCorsHeaders` of `ResponseEntityAssert`.
  
## [0.4.1] - 2018-08-28

### Changed
- CORS Filter allows all headers.

## [0.3.0] - 2018-08-27

This version upgrades dependencies, with focus on Spring Boot 2.0.4.

### Changed
- Upgraded Spring Boot autoconfigure from 2.0.3 to 2.0.4
- Upgraded Spring from 5.0.7 to 5.0.8
- Upgraded AWS from 1.11.349 to 1.11.396
- Upgraded Immutables Value from 2.6.1 to 2.7.1
- Upgraded assertJ from 3.10.0 to 3.11.0
- Upgraded mockito from 2.17.0 to 2.21.0
- Upgraded checkstyle from 8.10.1 to 8.12
- Upgraded checkstyle rules from 1.8.0 to 1.9.0
- Upgraded enforcer from 3.0.0-M1 to 3.0.0-M2
- Upgraded surefire from 2.21.0 to 2.22.0
- Upgraded jacoco from 0.8.1 to 0.8.2 

## [0.2.0] - 2018-06-23

### Added
- Added module `yak4j-mapping-test-utils`

## [0.1.1] - 2018-06-19

### Changed
- Upgraded checkstyle rules

## [0.1.0] - 2018-06-18

### Added
- Added module `yak4j-aws-dynamodb`

## [0.0.1]..[0.0.5] - 2018-06-17

- Initial release

[Unreleased]: https://github.com/ngeor/yak4j/compare/v0.9.0...HEAD
[0.9.0]: https://github.com/ngeor/yak4j/compare/v0.8.0...v0.9.0
[0.8.0]: https://github.com/ngeor/yak4j/compare/v0.7.1...v0.8.0
[0.7.1]: https://github.com/ngeor/yak4j/compare/v0.7.0...v0.7.1
[0.7.0]: https://github.com/ngeor/yak4j/compare/v0.6.0...v0.7.0
[0.6.0]: https://github.com/ngeor/yak4j/compare/v0.5.1...v0.6.0
[0.5.1]: https://github.com/ngeor/yak4j/compare/v0.4.1...v0.5.1
[0.4.1]: https://github.com/ngeor/yak4j/compare/v0.3.0...v0.4.1
[0.3.0]: https://github.com/ngeor/yak4j/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/ngeor/yak4j/compare/v0.1.1...v0.2.0
[0.1.1]: https://github.com/ngeor/yak4j/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/ngeor/yak4j/compare/v0.0.5...v0.1.0
[0.0.5]: https://github.com/ngeor/yak4j/compare/v0.0.1...v0.0.5
[0.0.1]: https://github.com/ngeor/yak4j/tree/v0.0.1
