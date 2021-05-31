# yak4j-swagger-maven-plugin

A Maven plugin which processes Swagger documents.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-swagger-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22yak4j-swagger-maven-plugin%22)

## Merge

With the merge goal you can merge multiple Swagger documents together. The scope
of the merge is limited to the `definitions` and `paths` elements of the Swagger
definition.

### Example

The following example combines two swagger files,
`src/main/swagger/address-book.yml` and `src/main/swagger/auth.yml`. The result
is stored in `target/swagger.yml`.

The plugin supports adding prefixes to definitions and paths in order to avoid
clashes.

```xml
<plugin>
  <groupId>com.github.ngeor</groupId>
  <artifactId>yak4j-swagger-maven-plugin</artifactId>
  <version>0.9.5</version>
  <executions>
    <execution>
      <id>merge</id>
      <goals>
        <goal>merge</goal>
      </goals>
      <configuration>
        <inputs>
          <input>
            <file>src/main/swagger/address-book.yml</file>
            <pathPrefix>/address-book</pathPrefix>
            <definitionPrefix>AddressBook</definitionPrefix>
          </input>
          <input>
            <file>src/main/swagger/auth.yml</file>
            <pathPrefix>/auth</pathPrefix>
            <definitionPrefix>Auth</definitionPrefix>
          </input>
        </inputs>
        <output>${project.build.directory}/swagger.yml</output>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### Configuration

| Name              | Required | Description                                                                          |
| ----------------- | :------: | ------------------------------------------------------------------------------------ |
| inputs            |   yes    | a collection of Swagger files to process                                             |
| output            |   yes    | the filename where the resulting Swagger file will be written                        |
| inlineDefinitions |    no    | a collection of partial Swagger files that will be inlined in the resulting document |

Each `input` element has these parameters:

- `file`: the filename where a Swagger file can be read from
- `pathPrefix`: all paths will be prefixed with this, in order to prevent
  clashes.

  For example, if the Swagger file defines the path `/create` and the
  `pathPrefix` is set to `/orders`, the resulting Swagger file will have a path
  `/orders/create`.

- `definitionPrefix`: all definitions (models) will be prefixed with this, in
  order to prevent clashes.

  For example, if the Swagger file defines the model `Address` and the
  `definitionPrefix` is set to `Order`, then the resulting definition will be
  `OrderAddress`.
