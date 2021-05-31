# yak4j-json-yaml-converter-maven-plugin

A Maven plugin which converts between JSON and YAML formats.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-json-yaml-converter-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22yak4j-json-yaml-converter-maven-plugin%22)

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
