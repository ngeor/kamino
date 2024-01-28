# yak4j-json-yaml-converter-maven-plugin

A Maven plugin which converts between JSON and YAML formats.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-json-yaml-converter-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.github.ngeor/yak4j-json-yaml-converter-maven-plugin/overview)
[![Build yak4j-json-yaml-converter-maven-plugin](https://github.com/ngeor/kamino/actions/workflows/build-plugins-yak4j-json-yaml-converter-maven-plugin.yml/badge.svg)](https://github.com/ngeor/kamino/actions/workflows/build-plugins-yak4j-json-yaml-converter-maven-plugin.yml)
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
