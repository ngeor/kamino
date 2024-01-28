# kamino

A monorepo for my hobby Java projects

## Folder structure

```txt
apps
  app1
archetypes
  archetype1
internal-tooling
  tool1
libs
  lib1
plugins
  plugin1
```

Each project is independent. There is no root parent pom for the entire monorepo.
It might even be in the future that some projects are not using Maven.

Each project's folder should have a README file and a CHANGELOG (if applicable).

`internal-tooling` holds solutions specific to managing this repository.

## Releasing

Releasing is tag based. The tag format is the folder structure,
followed by the version, e.g.: `apps/app1/v1.2.3`, `libs/lib1/v.1.2.3`, etc.

Example:

```sh
mvn -B -Dtag=libs/yak4j-dom/v1.9.7 release:prepare -DreleaseVersion=1.9.7 -DdevelopmentVersion=1.10.0-SNAPSHOT
mvn release:clean
```

Normally apps aren't released to Nexus.
