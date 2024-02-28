# Changelog

All notable changes to this project will be documented in this file.

## [4.9.0](https://github.com/ngeor/kamino/compare/libs/java/v4.8.0...libs/java/v4.9.0) - 2024-02-27

### Features

* Added openrewrite

### Miscellaneous Tasks

* Added badges
* Added dependency management for `com.puppycrawl.tools:checkstyle`

### Dependencies

* Upgrade assertJ to 3.25.3
* Upgrade checkstyle to 10.13.0
* Upgrade jUnit to 5.10.2
* Upgrade native maven plugin to 0.10.1
* Upgrade sortpom maven plugin to 3.4.0
* Upgrade javadoc maven plugin to 3.6.3
* Upgrade source maven plugin to 3.3.0
* Upgrade shade maven plugin to 3.5.2

## [4.8.0](https://github.com/ngeor/kamino/compare/libs/java/v4.7.1...libs/java/v4.8.0) - 2024-02-23

### Features

* sortpom tweaks
* Add dependency management for commons-io, commons-lang3, jsr305

### Miscellaneous Tasks

* Regenerate templates
* Use snapshot version of checkstyle rules, apply spotless to all files for now
* Switch to latest snapshot version of checkstyle-rules
* Switch to checkstyle rules 7.0.0

### Dependencies

* Upgrade to Mockito 5.10.0

## [4.7.1](https://github.com/ngeor/kamino/compare/libs/java/v4.7.0...libs/java/v4.7.1) - 2024-02-15

### Fixes

* Removed shade and native profiles from parent pom, broke libs

## [4.7.0](https://github.com/ngeor/kamino/compare/libs/java/v4.6.0...libs/java/v4.7.0) - 2024-02-14

### Features

* Introducing yak4j-argparse library

## [4.6.0](https://github.com/ngeor/kamino/compare/libs/java/v4.5.0...libs/java/v4.6.0) - 2024-02-13

### Features

* Using native image plugin

### Miscellaneous Tasks

* Use `com.github.ngeor:java` as the parent pom everywhere

## [4.5.0](https://github.com/ngeor/kamino/compare/libs/java/v4.4.0...libs/java/v4.5.0) - 2024-02-10

### Features

* Added spotless plugin management

### Miscellaneous Tasks

* Configure sortpom to not expand empty elements

## [4.4.0](https://github.com/ngeor/kamino/compare/libs/java/v4.3.0...libs/java/v4.4.0) - 2024-02-10

### Features

* Adding root pom (#11)
* Add dependency management for common test libraries

### Miscellaneous Tasks

* Delete old .github and scripts folders
* Keeping only root .editorconfig, deleting the rest
* Keeping only root .gitignore, deleting the rest
* Moved .gitattributes to the root folder

## [4.3.0](https://github.com/ngeor/kamino/compare/libs/java/v4.2.1...libs/java/v4.3.0) - 2024-01-30

### Features

* Added plugin management for maven source, javadoc and deploy plugins

### Fixes

* use maven effective pom to determine java version

## [4.2.1] - 2024-01-28

### Miscellaneous Tasks

* Add 'libs/java/' from commit '4247ca588e52b464446404a234665da63205ee88'
* Adjusted imported code

## [4.2.0] - 2024-01-26

### Miscellaneous Tasks

- Updated readme
- Upgraded dependencies

### Deps

- Updated dependency versions

## [4.1.1] - 2022-12-08

### Miscellaneous Tasks

- Fix deployment

## [4.1.0] - 2022-12-08

### Features

- Added sortpom

## [4.0.0] - 2022-12-08

### Miscellaneous Tasks

- [**breaking**] Upgraded to Java 17
- [**breaking**] Removed dependency management and most plugins
- Updated CI for Java 17

## [3.4.0] - 2022-11-17

### Miscellaneous Tasks

- Update dependency org.mockito:mockito-core to v4.8.0
- Update dependency org.apache.maven.plugins:maven-shade-plugin to v3.4.0
- Update dependency org.apache.maven.plugins:maven-jar-plugin to v3.3.0
- Update dependency org.codehaus.groovy:groovy-all to v3.0.13
- Update dependency org.junit.jupiter:junit-jupiter to v5.9.1
- Update dependency com.puppycrawl.tools:checkstyle to v10.3.4
- Updated auto-value to 1.10
- Updated MapStruct to 1.5.3
- Updated Jackson to 2.14.0
- Updated Maven Shade plugin to 3.4.1
- Updated checkstyle rules to 6.3.0
- Updated checkstyle to 10.4
- Updated Mockito to 4.9.0

## [3.3.0] - 2022-09-07

### Features

- Use tag based release workflow

### Miscellaneous Tasks

- Update changelog for 3.2.0
- Update dependency com.squareup.okhttp3:okhttp to v4.10.0
- Update dependency org.mapstruct:mapstruct to v1.5.2.final
- Update dependency com.puppycrawl.tools:checkstyle to v10.3.1
- Update dependency org.apache.maven.plugins:maven-deploy-plugin to v3.0.0
- Update dependency com.puppycrawl.tools:checkstyle to v10.3.3
- Update dependency org.apache.maven.plugins:maven-javadoc-plugin to v3.4.1
- Update dependency org.codehaus.groovy:groovy-all to v3.0.12
- Update dependency org.apache.maven.plugins:maven-checkstyle-plugin to v3.2.0
- Update dependency org.junit.jupiter:junit-jupiter to v5.9.0
- Update dependency org.mockito:mockito-core to v4.7.0
- Update jackson.version to v2.13.4
- Update dependency com.github.ngeor:checkstyle-rules to v6.2.1

## [3.2.0] - 2022-06-12

### Miscellaneous Tasks

- Update changelog for 3.1.1
- Add renovate.json (#19)
- Update dependency org.mockito:mockito-core to v4 (#20)
- Update dependency org.mockito:mockito-core to v4 (#20)
- Updated changelog
- Update dependency com.github.ngeor:checkstyle-rules to v5.1.0
- Updated changelog
- Update dependency org.apache.maven.plugins:maven-javadoc-plugin to v3.3.2
- Update dependency org.sonatype.plugins:nexus-staging-maven-plugin to v1.6.9
- Updated changelog
- Group dependencies separately in changelog
- Updated changelog
- Update dependency org.sonatype.plugins:nexus-staging-maven-plugin to v1.6.10
- Updated changelog
- Update dependency org.sonatype.plugins:nexus-staging-maven-plugin to v1.6.11
- Update dependency org.apache.maven.plugins:maven-compiler-plugin to v3.10.0
- Update dependency org.sonatype.plugins:nexus-staging-maven-plugin to v1.6.12
- Update dependency com.puppycrawl.tools:checkstyle to v10
- Update dependency org.codehaus.groovy:groovy-all to v3.0.10
- Update jackson.version to v2.13.2
- Update dependency org.mockito:mockito-core to v4.4.0
- Update dependency com.github.ngeor:checkstyle-rules to v5.2.0
- Update dependency org.apache.maven.plugins:maven-compiler-plugin to v3.10.1
- Update dependency org.apache.maven.plugins:maven-dependency-plugin to v3.3.0
- Update dependency com.github.ngeor:checkstyle-rules to v5.3.0
- Update dependency com.github.ngeor:checkstyle-rules to v6
- Update dependency com.fasterxml.jackson.core:jackson-databind to v2.13.2.1
- Update dependency com.fasterxml.jackson.core:jackson-databind to v2.13.2.2
- Update dependency com.puppycrawl.tools:checkstyle to v10.1
- Update dependency org.apache.maven.plugins:maven-shade-plugin to v3.3.0
- Update dependency org.apache.maven.plugins:maven-failsafe-plugin to v3.0.0-m6
- Update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.0.0-m6
- Update dependency org.jacoco:jacoco-maven-plugin to v0.8.8
- Update dependency org.sonatype.plugins:nexus-staging-maven-plugin to v1.6.13
- Update dependency com.puppycrawl.tools:checkstyle to v10.2
- Update dependency org.apache.maven.plugins:maven-javadoc-plugin to v3.4.0
- Update dependency org.mockito:mockito-core to v4.5.1
- Update jackson.version to v2.13.3
- Update dependency org.mockito:mockito-core to v4.6.0
- Update dependency com.puppycrawl.tools:checkstyle to v10.3
- Update dependency org.assertj:assertj-core to v3.23.1
- Update dependency org.codehaus.groovy:groovy-all to v3.0.11
- Update dependency org.mockito:mockito-core to v4.6.1
- Update dependency org.mapstruct:mapstruct to v1.5.0.final
- Update dependency org.mapstruct:mapstruct to v1.5.1.final
- Update dependency org.apache.maven.plugins:maven-failsafe-plugin to v3.0.0-m7
- Update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.0.0-m7
- Update dependency org.apache.maven.plugins:maven-release-plugin to v3.0.0-m6
- Update dependency org.apache.maven.plugins:maven-enforcer-plugin to v3.1.0
- Update dependency com.github.ngeor:checkstyle-rules to v6.1.0

## [3.1.1] - 2022-02-05

### Bug Fixes

- Generate changelog during release (#18)

### Miscellaneous Tasks

- Update changelog for 3.1.0

## [3.1.0] - 2022-02-05

### Features

- Install and use git-cliff during release GitHub Action

### Miscellaneous Tasks

- Updated changelog
- Updated ${checkstyle.version} from 9.2.1 to 9.3
- Updated changelog

## [3.0.0] - 2022-02-02

### Features

- Using properties to manage dependency and plugin versions
- [**breaking**] Upgrade checkstyle to 9.2.1

### Miscellaneous Tasks

- Updated changelog
- Updated pom properties

## [2.4.0] - 2022-01-27

### Bug Fixes

- Make release script executable upon copying to other repo

### Features

- Support finalizing release

## [2.3.0] - 2022-01-26

### Features

- Adding script to install workflows and release script in other repos
- Support initialization step in release script

### Miscellaneous Tasks

- Remove obsolete release files
- Updated changelog
- Excluding changelog commits from changelog
- Updated changelog
- Removed unused workflow
- Removed duplicate badge

## [2.2.1] - 2022-01-26

### Features

- Adding a Python script for releasing
- Performing the release with the Python script

### Miscellaneous Tasks

- Update readme

## [2.2.0] - 2022-01-23

### Features

- Testing release branch
- Performing release

## [1.0.0] - 2021-06-26

### Yak4j-cli

- Registering new module in parent pom
- Added picocli
- Added list command

<!-- generated by git-cliff -->
