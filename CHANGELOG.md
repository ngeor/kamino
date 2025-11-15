## [unreleased]

### 💼 Other

- *(libs/versions)* Switching to development version 2.1.0-SNAPSHOT

### 🎨 Styling

- Styling changelog according to default options

### ⚙️ Miscellaneous Tasks

- Updated copyright year
- Removed project yak4j-xml
- Moved archetype-quickstart-jdk8 back to its own repo
- Moved libs-yak4j-spring-test-utils back to its own repo
- Moved yak4j-utc-time-zone-mapper back to its own repo
- Moved yak4j-filename-conventions-maven-plugin back to its own repo
- Moved checkstyle-rules back to its own repo
- Moved yak4j-json-yaml-converter-maven-plugin back to its own repo
- Moved yak4j-swagger-maven-plugin back to its own repo
- Moved yak4j-sync-archetype-maven-plugin back to its own repo
- Moved zfs-snapshot-trimmer back to its own repo
- Referencing yak4j-dom from central repository
- Moved xmltrans back to its own repo
- Moved jTetris back to its own repo
- Moved jSpiderGame back to its own repo
- Moved jHangMan back to its own repo
- Moved jDirDiff back to its own repo
- Moved jCalendar back to its own repo
- Moved icqfriends back to its own repo
- Moved BuzzStats back to its own repo
- Using Java 21 as the default
- Using Java 21 as the default
- Test commit
- Moved java back to its own repo
- Moved krt back to its own repo
- Updated gitignore
- Updated editorconfig
- Removed .gitattributes
## [libs/versions/v2.0.0] - 2024-03-10

### 💼 Other

- *(plugins/yak4j-json-yaml-converter-maven-plugin)* Switching to development version 0.10.0-SNAPSHOT
- *(libs/versions)* Releasing 2.0.0
## [plugins/yak4j-json-yaml-converter-maven-plugin/v0.9.0] - 2024-03-10

### 💼 Other

- *(libs/yak4j-dom)* Switching to development version 1.12.0-SNAPSHOT
- *(plugins/yak4j-json-yaml-converter-maven-plugin)* Releasing 0.9.0
## [libs/yak4j-dom/v1.11.0] - 2024-03-10

### 💼 Other

- *(libs/yak4j-process)* Switching to development version 1.1.0-SNAPSHOT
- *(libs/yak4j-dom)* Releasing 1.11.0

### ⚙️ Miscellaneous Tasks

- Updated badges in README
## [libs/yak4j-process/v1.0.0] - 2024-03-10

### 🚀 Features

- Returning whether transformTextNodes had changes or not
- Added getTags method
- Made getCoordinates private in favor of coordinates method
- Performance optimization for coordinates method
- Added dependency management for immutables
- Added `getChildElementsAsIterable` method
- Resolve snapshots of reactor
- Added `CoordinatesVisitor`

### 🐛 Bug Fixes

- Support both file and directory values in parent pom relativePath
- Ensure XML documents have EOL at EOF

### 💼 Other

- *(libs/yak4j-utc-time-zone-mapper)* Switching to development version 0.19.0-SNAPSHOT
- Upgraded immutables to 2.10.1
- *(libs/yak4j-process)* Releasing 1.0.0

### 🚜 Refactor

- Rename `yak4j-argparse` to `argparse`
- Improve PomRepository design
- Removed findLoadedFile from PomRepository
- Extracted Options class for the options of MavenReleaser
- Extracted method initializeGitAndDoSanityChecks
- Extract GitInitializer function class
- Extract EffectivePomLoader function class
- Extract PomBasicValidator function class
- Composing functions to calculae coordinates and do basic pom validation
- Extracted VersionDiff class
- Extracted BackupFile class
- Use an Options object for ChangeLogUpdater
- Extracted RemoveParentElements function class
- Extracted EnsureNoSnapshotVersions function class
- Extracted UpdateScmTag function class
- Moved GitInitializer tests to their own file
- Added `GroupIdArtifactId` record
- Using `CoordinatesVisitor`

### ⚙️ Miscellaneous Tasks

- Suppress warning about usage of `System.out`
- Fix build
- Added cache for ModuleRepository
- Renamed Makefile target from rebuild to build
- Added phony targets to Makefile
- Deleted unused `deploy` method
- Added name and description to pom.xml
## [libs/yak4j-utc-time-zone-mapper/v0.18.0] - 2024-02-28

### 🚀 Features

- Added verify option in the Makefile

### 🐛 Bug Fixes

- NPE in case next version is not available

### 💼 Other

- *(plugins/yak4j-sync-archetype-maven-plugin)* Switching to development version 0.19.0-SNAPSHOT
- *(libs/yak4j-utc-time-zone-mapper)* Releasing 0.18.0

### ⚙️ Miscellaneous Tasks

- Extracted common element names into constants
- Removed method `getResolutionPhase` which was only used in tests
- Removed method getOriginalParentPom
- Do not remove "parent" element in PomRepository
## [plugins/yak4j-sync-archetype-maven-plugin/v0.18.0] - 2024-02-28

### 🚀 Features

- Include process output to the exception
- Ensure no snapshot versions exist in release pom
- Added parent pom for public libraries
- Support `poms` folder in internal tooling
- [**breaking**] Added tryParse methods, made parse methods return an exception instead of returning null
- Show what the next version will be in the overview command

### 🐛 Bug Fixes

- Moved java parent pom back to its old location for now
- Removed un-thrown exceptions from javadoc

### 💼 Other

- *(archetypes/archetype-quickstart-jdk8)* Switching to development version 3.1.0-SNAPSHOT
- *(plugins/yak4j-sync-archetype-maven-plugin)* Releasing 0.18.0

### ⚙️ Miscellaneous Tasks

- Use SemVerBump.tryParse method
## [archetypes/archetype-quickstart-jdk8/v3.0.0] - 2024-02-27

### 🚀 Features

- Implemented toString for ElementWrapper
- Added "modules" field in known element names
- Added "removeChildNodesByName" overload that accepts a Predicate

### 🐛 Bug Fixes

- Set scm/tag on release pom

### 💼 Other

- *(plugins/yak4j-filename-conventions-maven-plugin)* Switching to development version 0.20.0-SNAPSHOT
- *(archetypes/archetype-quickstart-jdk8)* Releasing 3.0.0

### ⚙️ Miscellaneous Tasks

- Use mvnd by default in helper scripts
## [plugins/yak4j-filename-conventions-maven-plugin/v0.19.0] - 2024-02-27

### 💼 Other

- *(plugins/yak4j-swagger-maven-plugin)* Switching to development version 0.19.0-SNAPSHOT
- *(plugins/yak4j-filename-conventions-maven-plugin)* Releasing 0.19.0

### ⚙️ Miscellaneous Tasks

- Improve argument parsing
## [plugins/yak4j-swagger-maven-plugin/v0.18.0] - 2024-02-27

### 🚀 Features

- Use annotated tags for releases
- Added method importNode

### 🐛 Bug Fixes

- Fix merging of plugin goals

### 💼 Other

- *(libs/java)* Switching to development version 4.10.0-SNAPSHOT
- *(plugins/yak4j-swagger-maven-plugin)* Releasing 0.18.0

### ⚙️ Miscellaneous Tasks

- Updated changelog
## [libs/java/v4.9.0] - 2024-02-27

### 💼 Other

- *(libs/yak4j-spring-test-utils)* Switching to development version 0.23.0-SNAPSHOT
- *(libs/java)* Releasing 4.9.0
## [libs/yak4j-spring-test-utils/v0.22.0] - 2024-02-27

### 💼 Other

- *(libs/yak4j-dom)* Switching to development version 1.11.0-SNAPSHOT
- *(libs/yak4j-spring-test-utils)* Releasing 0.22.0
## [libs/yak4j-dom/v1.10.0] - 2024-02-27

### 🚀 Features

- Add missing badges
- Added more options to build.sh script
- Added openrewrite
- Only returning ProcessFailedException
- Adding script to run OpenRewrite in a loop
- Pretty-print overview table
- Improve performance of overview command by parallelizing processing
- Add overload `findChildElements` to get elements of multiple names
- Added `firstElementsText` method
- Added method `deepClone`
- Implemented basic parent resolution in `PomRepository`
- Adding Input and Resolver abstractions
- Modified `firstElementsText` to accept and return an array
- *(yak4j-git)* Specify initial branch in git init
- Add deploy method, add support for custom settings file
- Adding hyperlinks in the changelog
- Skip enforcer and checkstyle in some steps as it is covered elsewhere
- Adding Makefile
- Make XML indentation configurable

### 🐛 Bug Fixes

- Use current date when generating the changelog of a 'future' release
- Run rewrite before spotless
- Exclude tags from other projects
- Removed internal dependencies from arturito
- Troubleshooting releases
- Accidentally kept snapshot version in release pom

### 💼 Other

- *(libs/java)* Switching to development version 4.9.0-SNAPSHOT
- Upgrade Dropwizard to 2.1.12
- Upgrade assertJ to 3.25.3
- Upgrade checkstyle to 10.13.0
- Upgrade jUnit to 5.10.2
- Upgrade native maven plugin to 0.10.1
- Upgrade sortpom maven plugin to 3.4.0
- Upgrade javadoc maven plugin to 3.6.3
- Upgrade source maven plugin to 3.3.0
- Upgrade shade maven plugin to 3.5.2
- *(libs/yak4j-dom)* Releasing 1.10.0

### 🚜 Refactor

- Adding ModuleFinder to yak4j-maven
- Adding MavenModuleNg
- Added sub-classes of MavenModuleNg
- Deleted old MavenModule
- Do not wrap IOException into a RuntimeException
- Use overload `findChildElements` to get elements of multiple names
- Added `PomRepository`
- Adding `ResolutionPhase`
- Removed MavenModule in favor of PomRepository
- Arranging code in packages
- Removed DOM logic from MavenCoordinates
- Removed DOM logic from ParentPom
- Removed `MavenDocument`
- Removed `ChildNodesIterator`
- Moved inner classes to upper level
- Make FileInput convert file to the canonical format
- Change isKnown to isUnknown
- Do not use a HashMap in firstElementsText
- Offer subclasses access to the process builder
- Use internal libraries
- Extracted `Formatter` class
- Extracted MarkdownMerger class
- Moved inner classes up
- Extract CommitInfoFactory class
- Extracted CommitGrouper class
- Extracted TitleComparator

### ⚙️ Miscellaneous Tasks

- Added badges
- Added badges
- Sortpom
- Added override annotations
- Removed un-thrown exceptions
- Added override annotations
- Using Java 17 instanceof
- Added override annotations
- Using Java 17 instanceof
- Removed un-thrown exception
- Use Java 17 instanceof
- Use Java 17 instanceof
- Removed un-thrown exceptions
- Remove un-thrown exceptions
- Removed un-thrown exceptions
- Removed duplicated elements present in parent pom
- Added dependency management for `com.puppycrawl.tools:checkstyle`
- Sortpom
- Update changelog
- Make test method public
- Fix build
- Fix build
- Remove warnings from build
- Adding tests
- Use new `firstElementsText` method
- Use `transformTextNodes` method
- Make load method private
- Deprecated 3 methods
- Reuse DocumentBuilder
- Do not subclass `LazyInitializer`
- Skip tests of internal tooling during a release
- Updated changelog
- Correct names of SubGroupOptions fields
- Updated changelog
## [libs/java/v4.8.0] - 2024-02-23

### 🚀 Features

- Conditionally build native images
- Sortpom tweaks
- Add another commit exclusion rule for sorting pom
- Render breaking changes in the changelog
- Support deps type
- [**breaking**] Reading structured outline with nested sections
- Auto-generate help about the parameters
- Support normalizing parsed values
- Offer flag to overwrite existing changelog entries
- Support "refactor" type
- Added modelVersion, name, description methods
- Adding sanity checks about the `pom.xml`
- Added method getTextContentTrimmedAsStream
- Added "asTyped" method, converting XML document into Java record
- Support child elements
- Implement sanity checks before attempting release
- Introducing Preconditions API
- Added sanity checks against the Maven pom file before preparing the release
- Add dependency management for commons-io, commons-lang3, jsr305
- Added methods getCurrentBranch, checkoutNewBranch, ensureOnDefaultBranch, clone, symbolicRef
- Added methods hasNonStagedChanges, lsFiles
- Added fetch method
- Added git sanity checks
- Added methods initAndConfigureIdentity, configureIdentity
- Adding new library for generating changelog
- Update changelog during release

### 🐛 Bug Fixes

- Workaround against bytebuddy not able to make some mocks under Java 21
- Ensure maven modules are sorted
- Support tags at HEAD
- Do not generate unreleased section if everything is released
- Lost tags on ignored commits
- Do not generate empty releases

### 💼 Other

- *(libs/versions)* Switching to development version 1.1.0-SNAPSHOT
- *(libs/java)* Releasing 4.8.0

### 🚜 Refactor

- Move markdown code to its own package
- Extract generateChangeLog method
- Sanitize path during argument parsing
- Extract BaseCommand base class for sub-commands
- Moved grouping logic to "ReleaseGrouper" class
- Moved code to `mr` package
- Converted TagPrefix into a non-static class
- Moved code to `maven` package
- Encapsulation of XML document wrapper inside `MavenDocument`
- Moved effective pom logic into MavenDocument
- Move validation tests to nested class
- Moved code to `git` sub-package
- Moved code to `process` sub-package
- Move code to the `changelog` library

### ⚙️ Miscellaneous Tasks

- Updated readme
- Add executable mode to build.sh
- Removed unused code
- Spotless
- Sort pom
- Using assertJ's isEqualToNormalizingNewlines in the tests
- Fixed disabled test
- Add a few more tests
- Move to argparse package
- Spotless
- [**breaking**] Drop version parameter
- Using method getTextContentTrimmedAsStream
- Sortpom
- Use dependency management for commons-io, commons-lang3, jsr305 from parent pom
- Use commons-lang3 library
- Use commons-lang3 library
- Use TempDir annotation in tests
- Use methods initAndConfigureIdentity, configureIdentity
- Downgrade to Java 17
- Updated readme
- Spotless
- Fix parent pom
## [libs/versions/v1.0.1] - 2024-02-18

### 🐛 Bug Fixes

- Adding name and description to pom
- Exclude release commits from changelog

### 💼 Other

- *(libs/versions)* Switching to development version 1.1.0-SNAPSHOT
- *(libs/versions)* Releasing 1.0.1
## [libs/versions/v1.0.0] - 2024-02-18

### 🚀 Features

- Support `--tags` and `--follow-tags`
- Offer overload methods that accept arguments as collection
- Add more exclusion filters
- Support generating changelog for all modules
- Add method `TagPrefix#tag`
- Introducing `Tag` type to disambiguate between tags and versions
- Use maven versions plugin instead of maven release plugin for the release

### 🐛 Bug Fixes

- Push tags upon releasing
- Fix setting the next snapshot version

### 💼 Other

- *(libs/versions)* Releasing 1.0.0

### 🚜 Refactor

- Extracted ModuleFinder and ChangeLogUpdater classes

### ⚙️ Miscellaneous Tasks

- Use collection-based overloads
- Spotless
- Updated changelog
- Rename yak4j-semver to versions
- Moved versions into its own package
## [libs/yak4j-xml/v0.18.0] - 2024-02-18

### 🚀 Features

- Added parseString and writeToString methods
- Added updateVersion method
- Add dry run and no push support
- Added removeChildNodesByName method
- Added resetOne and deleteTag methods
- Exclude pom.xml from modification checks
- Generating the release over the effective pom
- Ability to re-indent a document
- Added getChildElementsAsIterator
- Trimming whitespace out of text nodes
- Added path method
- Support merging build plugins
- Improved effective pom resolution
- Use new effective pom resolution
- Improved effective pom resolution

### 🐛 Bug Fixes

- Use a temporary file for backing up pom.xml
- Fix build
- Merge child into parent
- Support plugin executions
- Merge plugin configurations
- Merge plugin configurations

### 🚜 Refactor

- Move some logic into ParentPom record
- Return document from `PomMerger.merge`
- Builder-style API in PomMerger to avoid mixing parent and child parameters

### ⚙️ Miscellaneous Tasks

- Updated changelog
- Switch to latest snapshot version of checkstyle-rules
- Removed unused class XmlUtils
- Spotless
- Add tests for merging pom documents
- Spotless
- Removed unused methods
- Add groupId to pom
- Regenerate workflows
- Improve error message
- Switch to checkstyle rules 7.0.0
## [libs/checkstyle-rules/v7.0.0] - 2024-02-17

### 🚀 Features

- Support relative path poms
- Take internal parent poms into account as dependencies
- Added getMostRecentTagWithDate
- Offer a "what is unreleased" overview by default
- [**breaking**] Changed import order to align with palantir
- Attempt to deploy checkstyle-rules with java parent pom in one go

### 🐛 Bug Fixes

- Arturito tooling needs Java 17, therefore the release workflows should follow that
- Process was blocking when stderr had content
- Fix failing tests
- Fix failing tests
- Remove quotes from format argument
- Revert to old way of deploying
- Breaking checkstyle-rules parent pom relationship

### 💼 Other

- Upgraded parent pom
- Upgrade to Mockito 5.10.0
- Upgrade to Spring 5.3.32

### ⚙️ Miscellaneous Tasks

- Removed duplicate dependencyManagement
- Regenerate templates
- Fix native image
- Switch to snapshot version of parent pom
- Remove unnecessary mock
- Added tip in readme
- Propagate ProcessFailedException
- Propagate ProcessFailedException
- Propagating ProcessFailedException
- Use snapshot version of checkstyle rules, apply spotless to all files for now
- Apply spotless
- Added readme tip
## [libs/java/v4.7.1] - 2024-02-15

### 🚀 Features

- Adding parent pom only for cli apps

### 🐛 Bug Fixes

- Removed shade and native profiles from parent pom, broke libs
## [libs/java/v4.7.0] - 2024-02-14

### 🚀 Features

- Introducing yak4j-semver library
- Working on an argument parser
- Working on semantic version calculator
- Introducing yak4j-argparse library
- Integrated version calculator in release tooling

### 🐛 Bug Fixes

- Fix build
- Change order of SemVerBump members so that patch is the smallest, in case of comparisons

### ⚙️ Miscellaneous Tasks

- Added missing javadoc comment
- Spotless
- Updated changelog
- Fixed build
- Updated changelog
## [libs/java/v4.6.0] - 2024-02-13

### 🚀 Features

- Added archetype for kamino apps
- Implemented rev-list retrieval
- Implementing changelog generator
- Introducing yak4j-markdown library
- Implemented changelog solution
- Using native image plugin

### 🐛 Bug Fixes

- Deadlock waiting for process
- Fine-tuning formatting
- Include resources in native image

### 🚜 Refactor

- Rename Readme to Markdown

### ⚙️ Miscellaneous Tasks

- Upgraded to latest parent pom
- Bootstrapping new project for changelog solution
- Use `com.github.ngeor:java` as the parent pom everywhere
- Fix build
- Fix build
- Updated changelog
- Updated changelog
## [libs/java/v4.5.0] - 2024-02-10

### 🚀 Features

- Added support for preparing library releases
- Added spotless plugin management

### ⚙️ Miscellaneous Tasks

- Configure sortpom to not expand empty elements
- Upgrade to latest parent pom
- Fixing build
- Fixing build
- Fix archetype-quickstart-jdk8 workflows
- [**breaking**] Remove checkstyle as it no longer works on Java 8
## [libs/java/v4.4.0] - 2024-02-10

### 🚀 Features

- Adding root pom (#11)
- Added yak4j-process library
- Added yak4j-git library
- Added yak4j-maven library
- Resolve properties in custom effective pom resolver
- Resolve parent pom in custom effective pom resolver
- Switch to custom effective pom calculator
- Add dependency management for common test libraries

### 🐛 Bug Fixes

- Resolve dependencies recursively
- Fix 1.8 version appearing accidentally in GitHub actions
- Upgrading libraries to Java 17

### 🚜 Refactor

- Add Box class
- Maven#effectivePom returns a DocumentWrapper

### ⚙️ Miscellaneous Tasks

- Delete old .github and scripts folders
- Keeping only root .editorconfig, deleting the rest
- Keeping only root .gitignore, deleting the rest
- Moved .gitattributes to the root folder
- README adjustments
- Added missing README files
- Remove build warnings
- Adding some tests to yak4j-maven
## [libs/java/v4.3.0] - 2024-01-30

### 🚀 Features

- Added skeleton project for krt (kamino release tool)
- Ensuring git is at the default branch
- Implementing npm release
- Supporting more configuration options
- Detecting git directory location
- Support pip project
- Support setting snapshot version
- [**breaking**] Rewrite krt in Java
- Add native image to the pipeline
- Support setting version in pom.xml files
- Validate input versions are sem ver
- [**breaking**] Support bumping by specifying major, minor or patch.
- Add version information
- Added plugin management for maven source, javadoc and deploy plugins

### 🐛 Bug Fixes

- Do not perform release for apps
- Do not commit if there are no pending changes
- Use maven effective pom to determine java version
- Ensuring all Maven commands have the arguments `-B -ntp`
- *(CI)* Fixing build
- Copy dependencies to output directory
- Fix git directory detection
- Normalize current directory
- Corrected changelog addition
- Support tag pattern for monorepo projects
- Include resources in native image
- Empty pom.xml in Windows
- Xml issues on Graal image on Windows
- Fixing native jobs
- Fixing native jobs
- Fixing workflows
- Fixing workflows

### 💼 Other

- Move classes to dtos namespace

### 🚜 Refactor

- Extract constant
- Add DirContext class
- Add GitTagPrefix class

### ⚙️ Miscellaneous Tasks

- Adding cliff.toml
- *(changelog)* Updated changelog
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- Migrated tests to jUnit 5
- Added test dependencies
- Build on JDK 17
- Adjusted imported code
- Adding cliff.toml
- *(changelog)* Updated changelog
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Add test for SimpleStringTemplate
- Added test for SemVer
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Update JaCoCo thresholds
- *(release)* Setting snapshot version for next development iteration
- *(release)* Setting snapshot version for next development iteration
- Added readme
- Added download section in readme
- Adding cliff.toml
- Adding LICENSE
- Adding .github\workflows\maven.yml
- Adding .github\FUNDING.yml
- Added CI workflows
- Adjusted imported code
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Adjusted imported code
- Updated copyright year in LICENSE
- Adding cliff.toml
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix checkstyle issues
- Adjusted imported code
- Upgraded to Java 17, upgraded dependencies
- Added assertJ
- Upgrade deps and apply spotless
- Adjusted imported code
- Updated gitignore
## [plugins/yak4j-sync-archetype-maven-plugin/v0.17.2] - 2024-01-28

### 🚀 Features

- *(ci)* Use tag based release workflow

### ⚙️ Miscellaneous Tasks

- Use release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix import order
- Adjusted imported code
## [plugins/yak4j-swagger-maven-plugin/v0.17.2] - 2024-01-28

### 🚀 Features

- *(ci)* Use tag based release workflow

### ⚙️ Miscellaneous Tasks

- Use release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix import order
- Adjusted imported code
## [plugins/yak4j-json-yaml-converter-maven-plugin/v0.8.2] - 2024-01-28

### 🚀 Features

- *(ci)* Use tag based release workflow

### ⚙️ Miscellaneous Tasks

- Use release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix import order
- Adjusted imported code
## [libs/java/v4.2.1] - 2024-01-28

### 🚀 Features

- Testing release branch
- Performing release
- Adding a Python script for releasing
- Performing the release with the Python script
- Adding script to install workflows and release script in other repos
- Support initialization step in release script
- Support finalizing release
- Using properties to manage dependency and plugin versions
- [**breaking**] Upgrade checkstyle to 9.2.1
- Install and use git-cliff during release GitHub Action
- *(ci)* Use tag based release workflow
- Added sortpom

### 🐛 Bug Fixes

- Make release script executable upon copying to other repo
- Generate changelog during release (#18)

### 💼 Other

- Registering new module in parent pom
- Added picocli
- Added list command
- Updated dependency versions

### ⚙️ Miscellaneous Tasks

- Update readme
- Remove obsolete release files
- *(changelog)* Updated changelog
- Excluding changelog commits from changelog
- *(changelog)* Updated changelog
- Removed unused workflow
- Removed duplicate badge
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Update changelog for 3.1.0
- *(changelog)* Update changelog for 3.1.1
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Group dependencies separately in changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Update changelog for 3.2.0
- [**breaking**] Upgraded to Java 17
- [**breaking**] Removed dependency management and most plugins
- Updated CI for Java 17
- Fix deployment
- Updated readme
- Adjusted imported code
## [libs/checkstyle-rules/v6.7.2] - 2024-01-28

### 🚀 Features

- [**breaking**] Upgrade to checkstyle 9.2.1
- [**breaking**] Change import order, relax javadoc rules
- *(ci)* Use tag based release

### 🐛 Bug Fixes

- Removed redundant properties from gpg plugin
- *(ci)* Fix CI pipeline
- Fix release script

### 💼 Other

- Upgraded checkstyle to 10.12.0
- Upgrade checkstyle to 10.12.1

### ⚙️ Miscellaneous Tasks

- Using release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updating changelog
- *(changelog)* Updated changelog
- Use release script from java repo
- *(changelog)* Update changelog for 5.1.0
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Update changelog for 5.2.0
- Set Java version to 11
- *(changelog)* Update changelog for 5.3.0
- *(changelog)* Update changelog for 6.0.0
- Fix the build
- *(changelog)* Update changelog for 6.0.1
- *(changelog)* Update changelog for 6.1.0
- [**breaking**] Upgrade to Java 17
- Use JDK 17 on GitHub CI
- Troubleshooting gpg in CI
- Troubleshooting CI
- Troubleshooting CI
- Troubleshooting CI
- Troubleshooting CI
- [**breaking**] Do not follow semver anymore (#42)
- Upgraded to checkstyle 10.7.0
- Upgrade checkstyle
- [**breaking**] Revert to previous Maven release workflow (tag based)
- Adjusted imported code
## [plugins/yak4j-filename-conventions-maven-plugin/v0.18.2] - 2024-01-28

### 🚀 Features

- *(ci)* Use tag based release workflow

### 🐛 Bug Fixes

- Render release workflow for plugins
- Do not import git subtree if already imported
- Test if project builds before and after importing
- Test if project builds before performing release

### 💼 Other

- Making importer accept arguments

### ⚙️ Miscellaneous Tasks

- Use release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix import order
- Adjusted imported code
- Adjusted imported code
## [libs/yak4j-utc-time-zone-mapper/v0.17.2] - 2024-01-28

### 🚀 Features

- *(ci)* Use tag based release workflow

### 💼 Other

- Working on project importer

### ⚙️ Miscellaneous Tasks

- Use release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix import order
- Adjusted imported code
## [libs/yak4j-spring-test-utils/v0.21.2] - 2024-01-28

### 🚀 Features

- *(ci)* Use tag based release workflow

### 💼 Other

- Working on project importer
- Working on project importer
- Working on project importer

### ⚙️ Miscellaneous Tasks

- Use release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix import order
- Adjusted imported code
## [archetypes/archetype-quickstart-jdk8/v2.8.4] - 2024-01-27

### 🚀 Features

- Using release script from java repository
- *(ci)* Use tag based release workflow

### 🐛 Bug Fixes

- Fixing release github workflow

### ⚙️ Miscellaneous Tasks

- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Updated .editorconfig
## [libs/yak4j-xml/v0.17.3] - 2024-01-27

### 🐛 Bug Fixes

- Fix URLs in pom.xml, added description
- Check maven deploy exit code
## [libs/yak4j-dom/v1.9.7] - 2024-01-27

### 🐛 Bug Fixes

- Fix URLs in pom.xml, added description
## [libs/yak4j-dom/v1.9.2] - 2024-01-26

### 🚀 Features

- *(ci)* Use tag based release workflow
- *(ci)* Use tag based release workflow
- Implementing release tool

### 🐛 Bug Fixes

- Fix import order

### ⚙️ Miscellaneous Tasks

- Using new release script
- Update release script
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Fix imports
- Using release script from java repo
- Updated copyright year in LICENSE
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- *(changelog)* Updated changelog
- Upgrade pom to Java 21
