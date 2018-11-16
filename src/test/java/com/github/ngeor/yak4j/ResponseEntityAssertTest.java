package com.github.ngeor.yak4j;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test for {@link ResponseEntityAssert}.
 */
class ResponseEntityAssertTest {

    @Test
    void hasStatus() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        Assertions.assertThat(responseEntity)
            .hasStatus(HttpStatus.I_AM_A_TEAPOT);
    }

    @Test
    void isOk() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Assertions.assertThat(responseEntity)
            .isOk();
    }

    @Test
    void isBadRequest() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity)
            .isBadRequest();
    }

    @Test
    void isForbidden() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Assertions.assertThat(responseEntity)
            .isForbidden();
    }

    @Test
    void isInternalServerError() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity)
            .isInternalServerError();
    }

    @Test
    void hasBody() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("hello world", HttpStatus.OK);
        Assertions.assertThat(responseEntity)
            .hasBody("hello world");
    }

    @Test
    void isUnauthorized() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(responseEntity)
            .isUnauthorized();
    }

    @Test
    void isCreated() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        Assertions.assertThat(responseEntity)
            .isCreated();
    }

    @Test
    void isConflict() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
        Assertions.assertThat(responseEntity)
            .isConflict();
    }
}
