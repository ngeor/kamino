## Overview

yak shaving for Java: Testing utilities for Spring

The package offers custom assertions for easier testing with Spring. Assertions
are built on top of [assertJ](https://joel-costigliola.github.io/assertj/).

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
