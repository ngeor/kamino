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
Only the (equivalent of the ) `release:prepare` step is done on the local machine.
The (equivalent of) the `release:perform` is done on the CI server.

The `maven-release-plugin` is not used. The custom tooling that is used
lives in `internal-tooling/changes` and uses the `maven-versions-plugin`
under the hood.

Normally, apps aren't released to Nexus.

## `null` vs `Optional`

- No method should ever return `null`.
- Accepting a `null` parameter can be tolerated.
- Returning an `Optional` if preferred over returning `null`.

## Third party libraries

The following third party libraries are preferred:

- Apache Commons Lang3
- jUnit 5
- AssertJ
- Mockito

## Tips

- Upgrade the parent pom of all modules with `mvn versions:update-parent -DallowSnapshots`
- Build native image of internal tools with `mvn -Pnative -am -pl internal-tooling/changes/ package -DskipTests`
- The `build.sh` script does some re-building of everything and re-generates GitHub workflows
