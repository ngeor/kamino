# kamino

A monorepo for my hobby Java projects

## Folder structure

```txt
archetypes
  archetype1
cli
  cli1
gui
  gui1    
internal-tooling
  tool1
libs
  lib1
plugins
  plugin1
web
  web1  
```

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

Normally, apps aren't released to Nexus.
