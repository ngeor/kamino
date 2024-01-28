# yak4j-json-yaml-converter-maven-plugin

A Maven plugin which converts between JSON and YAML formats.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-json-yaml-converter-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22yak4j-json-yaml-converter-maven-plugin%22)
[![Java CI with Maven](https://github.com/ngeor/yak4j-json-yaml-converter-maven-plugin/actions/workflows/maven.yml/badge.svg)](https://github.com/ngeor/yak4j-json-yaml-converter-maven-plugin/actions/workflows/maven.yml)
[![javadoc](https://javadoc.io/badge2/com.github.ngeor/yak4j-json-yaml-converter-maven-plugin/javadoc.svg)](https://javadoc.io/doc/com.github.ngeor/yak4j-json-yaml-converter-maven-plugin)

## Example

```xml
<plugin>
  <groupId>com.github.ngeor</groupId>
  <artifactId>yak4j-json-yaml-converter-maven-plugin</artifactId>
  <version>0.0.4</version>
  <executions>
    <execution>
      <id>yaml2json</id>
      <goals>
        <goal>yaml2json</goal>
      </goals>
      <configuration>
        <sourceDirectory>src/main/yaml</sourceDirectory>
        <includes>
          <include>*.yml</include>
        </includes>
        <outputDirectory>target/json</outputDirectory>
      </configuration>
    </execution>
  </executions>
</plugin>
```
