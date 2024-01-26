# kamino

A monorepo for my hobby Java projects

## Folder structure

```txt
apps
  app1
libs
  lib1
maven-plugins
  plugin1
```

Each project is independent. There is no root parent pom for the entire monorepo.
It might even be in the future that some projects are not using Maven.

Each project's folder should have a README file and a CHANGELOG (if applicable).

## Releasing

Releasing is tag based. The tag format is the folder structure,
followed by the version, e.g.: `apps/app1/v1.2.3`, `libs/lib1/v.1.2.3`, etc.

Normally apps aren't released to Nexus.
