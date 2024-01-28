# yak4j-sync-archetype-maven-plugin

A Maven plugin which updates your project from its archetype, replacing only the
files that you want.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-sync-archetype-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.github.ngeor/yak4j-sync-archetype-maven-plugin/overview)
[![Build yak4j-sync-archetype-maven-plugin](https://github.com/ngeor/kamino/actions/workflows/build-plugins-yak4j-sync-archetype-maven-plugin.yml/badge.svg)](https://github.com/ngeor/kamino/actions/workflows/build-plugins-yak4j-sync-archetype-maven-plugin.yml)
[![javadoc](https://javadoc.io/badge2/com.github.ngeor/yak4j-sync-archetype-maven-plugin/javadoc.svg)](https://javadoc.io/doc/com.github.ngeor/yak4j-sync-archetype-maven-plugin)

## Sync

Generates a temporary project based on an archetype and copies a selection of
files into the current project.

This can be useful when you have an archetype that you used to create the
project and you want to update some files based on the latest version of the
archetype.

### Example

```xml
<plugin>
  <groupId>com.github.ngeor</groupId>
  <artifactId>yak4j-sync-archetype-maven-plugin</artifactId>
  <version>0.9.5</version>
  <executions>
    <execution>
      <id>sync</id>
      <goals>
        <goal>sync</goal>
      </goals>
      <configuration>
        <archetypeGroupId>com.acme</archetypeGroupId>
        <archetypeArtifactId>some-archetype</archetypeArtifactId>
        <parameters>
          <favoriteColor>blue</favoriteColor>
        </parameters>
        <includes>
            <param>pom.xml</param>
            <param>src/**/*.java</param>
        </includes>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### Configuration

| Name                | Required | Description                                       |
| ------------------- | :------: | ------------------------------------------------- |
| archetypeGroupId    |   yes    | The group ID of the archetype                     |
| archetypeArtifactId |   yes    | The artifact ID of the archetype                  |
| archetypeVersion    |    no    | The version of the archetype                      |
| parameters          |    no    | Extra parameters that the archetype requires      |
| includes            |   yes    | The set of files to copy into the current project |
| excludes            |    no    | A set of files to exclude when copying            |
