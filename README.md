# yak4j-spring-test-utils
Testing utilities for Spring

[![Maven Central](https://img.shields.io/maven-central/v/com.github.ngeor/yak4j-spring-test-utils.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ngeor%22%20AND%20a:%22yak4j-spring-test-utils%22)

[![Build Status](https://travis-ci.org/ngeor/yak4j-spring-test-utils.svg?branch=master)](https://travis-ci.org/ngeor/yak4j-spring-test-utils)

## Maven

```xml
<dependency>
  <groupId>com.github.ngeor</groupId>
  <artifactId>yak4j-spring-test-utils</artifactId>
  <version>0.9.1</version>
  <scope>test</scope>
</dependency>
```

## Usage

The package offers custom assertions for easier testing with Spring.
Assertions are built on top of [assertJ](https://joel-costigliola.github.io/assertj/).


### ResponseEntity

Checking of status and body:

```java
import static com.github.ngeor.yak4j.Assertions.assertThat;

ResponseEntity<String> responseEntity = ResponseEntity.ok("hello world");

assertThat(responseEntity)
    .isOk() // check status = 200
    .hasBody("hello world"); // check body
```

### ResultActions

Checking status:

```java
import static com.github.ngeor.yak4j.Assertions.assertThat;

ResultActions resultActions = mockMvc.perform(/* do the mock MVC call */);

assertThat(resultActions)
    .isOk() // check status = 200
```

Checking for validation errors:

```java
import static com.github.ngeor.yak4j.InvalidFieldExpectationBuilder.invalidField;

assertThat(resultActions)
    .isBadRequest( // check status = 400
        invalidField("name", "NotNull") // check these validation errors
    );
```
