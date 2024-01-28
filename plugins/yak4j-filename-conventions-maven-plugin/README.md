# yak4j-filename-conventions-maven-plugin

A Maven plugin which enforces filename conventions.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-filename-conventions-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22yak4j-filename-conventions-maven-plugin%22)
[![Java CI with Maven](https://github.com/ngeor/yak4j-filename-conventions-maven-plugin/actions/workflows/maven.yml/badge.svg)](https://github.com/ngeor/yak4j-filename-conventions-maven-plugin/actions/workflows/maven.yml)
[![javadoc](https://javadoc.io/badge2/com.github.ngeor/yak4j-filename-conventions-maven-plugin/javadoc.svg)](https://javadoc.io/doc/com.github.ngeor/yak4j-filename-conventions-maven-plugin)

## Maven

```xml
<plugin>
  <groupId>com.github.ngeor</groupId>
  <artifactId>yak4j-filename-conventions-maven-plugin</artifactId>
  <version>0.10.2</version>
</plugin>
```

## Goals

The `check` goal allows you to enforce naming conventions on your filenames
using regular expressions.

### Example

The following execution will ensure that all SQL scripts in the
`src/main/resources` directory have only lowercase letters in their filename.

```xml
<plugin>
  <groupId>com.github.ngeor</groupId>
  <artifactId>yak4j-filename-conventions-maven-plugin</artifactId>
  <version>${yak4j.version}</version>
  <executions>
    <execution>
      <id>check</id>
      <goals>
        <goal>check</goal>
      </goals>
      <configuration>
        <directory>src/main/resources</directory>
        <includes>*.sql</includes>
        <pattern>[a-z]+\.sql</pattern>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### Configuration

| Parameter | Type     | Required | Description                                                      |
| --------- | -------- | :------: | ---------------------------------------------------------------- |
| directory | String   |   yes    | The root directory to search files                               |
| includes  | String[] |   yes    | A pattern that determines which files will be included           |
| excludes  | String[] |    no    | An optional pattern that determines which files will be excluded |
| pattern   | String[] |   yes    | A regular expression that much match the filenames of the files  |

The array parameters can be specified in two ways:

```xml
<includes>
  <param>*.sql</param>
  <param>*.txt</param>
</includes>
```

```xml
<includes>*.sql,*.txt</includes>
```

The `pattern` will need to match the entire filename. The filename will include
the subdirectory relative to the `directory` parameter.

Example: If `directory` is `/tmp` and the matched file is `/tmp/abc/def.txt`,
then the regular expression will need to match `abc/def.txt`.

If multiple patterns are specified, the matching filenames will need to comply
to at least one of the patterns.
