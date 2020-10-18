# yak4j-bitbucket-maven-plugin

A Maven plugin that can create Bitbucket tags and ensures the version in pom.xml
is not conflicting with an existing tag.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-bitbucket-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22yak4j-bitbucket-maven-plugin%22)

## Goals

| Goal                        | Description                                                                                                                                 |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| `ensure-tag-does-not-exist` | Breaks the build if a git tag that matches the pom version already exists.                                                                  |
| `publish-tag`               | Publishes a git tag using the version defined in `pom.xml`                                                                                  |
| `ensure-modules-version`    | Breaks the build if a child module declares a parent version different that the version of the main `pom.xml`                               |
| `ensure-pom-property`       | Breaks the build if a certain property in `pom.xml` is not the same as the version of `pom.xml`                                             |
| `ensure-readme-version`     | Breaks the build if the `README.md` has a line that matches `<version>...</version>` with a version other than the one defined in `pom.xml` |

## `ensure-tag-does-not-exist`

This goal runs at the `VALIDATE` phase by default. It will fail the build if the
version defined in the `pom.xml`:

-   is not a valid semantic version
-   would leave a gap in the semver sequence, based on the biggest git tag

Example: if the biggest git tag is `v1.2.3`, then the build will fail, unless
the pom version is one of `1.2.4`, `1.3.0`, `2.0.0`.

This goal will ignore snapshot versions.

### Configuration Parameters

-   `username` and `password` are the credentials for the Bitbucket Cloud REST
    API
-   `owner` and `slug` identify the git repository
-   `hash` points to the current git SHA

## `publish-tag`

This goal runs at the `DEPLOY` phase by default. It publishes a tag in git using
the Bitbucket Cloud REST API. The tag is named after the pom version, prefixed
with the letter v.

This goal will fail if the version in `pom.xml` is a snapshot version.

This goal uses the same configuration parameters as `ensure-tag-does-not-exist`.

## `ensure-modules-version`

This goal is useful for a multi-module project. It ensures that the version
defined in the main `pom.xml` matches the parent version defined in the modules.

This goal does not have any configuration parameters.

## `ensure-pom-property`

This goal checks that the value of a specific property matches the version of
`pom.xml`.

### Configuration Parameters

-   `propertyName` the name of the property whose value should match the version
    in the `pom.xml`.

## `ensure-readme-version`

This goal checks the `README.md` file for lines matching
`<version>...</version>` and breaks the build if that version does not match
`pom.xml`.

This is useful when the `README.md` has examples of using a project and the
version is not up to date.

This goal does not have any configuration parameters.

## Configuration Example

```xml
<profiles>
  <profile>
    <id>ci-only</id>
    <activation>
      <property>
        <name>env.CI</name>
        <value>true</value>
      </property>
    </activation>
    <build>
      <plugins>
        <plugin>
          <groupId>com.github.ngeor</groupId>
          <artifactId>yak4j-bitbucket-maven-plugin</artifactId>
          <version>0.5.1</version>
          <executions>
            <execution>
              <id>static-checks</id>
              <goals>
                <goal>ensure-modules-version</goal>
                <goal>ensure-readme-version</goal>
                <goal>ensure-pom-property</goal>
              </goals>
            </execution>
            <execution>
              <id>ensure-tag-does-not-exist</id>
              <goals>
                <goal>ensure-tag-does-not-exist</goal>
              </goals>
            </execution>
            <execution>
              <id>publish-tag</id>
              <goals>
                <goal>publish-tag</goal>
              </goals>
            </execution>
          </executions>
          <inherited>false</inherited>
          <configuration>
            <username>${env.BITBUCKET_USERNAME}</username>
            <password>${env.BITBUCKET_PASSWORD}</password>
            <owner>${env.BITBUCKET_REPO_OWNER}</owner>
            <slug>${env.BITBUCKET_REPO_SLUG}</slug>
            <hash>${env.BITBUCKET_COMMIT}</hash>
            <propertyName>my-special-property</propertyName>
          </configuration>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```
