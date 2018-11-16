package com.github.ngeor.yak4j;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Assertion DSL for {@link ResponseEntity}.
 *
 * @param <T> The body of the response.
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class ResponseEntityAssert<T> extends AbstractAssert<ResponseEntityAssert<T>, ResponseEntity<T>> {
    ResponseEntityAssert(ResponseEntity<T> responseEntity) {
        super(responseEntity, ResponseEntityAssert.class);
    }

    /**
     * Verifies that the response has the given HTTP status code.
     *
     * @param httpStatus The expected HTTP status code.
     * @return This instance.
     */
    public ResponseEntityAssert<T> hasStatus(HttpStatus httpStatus) {
        isNotNull();
        HttpStatus actualStatusCode = actual.getStatusCode();
        Assertions.assertThat(actualStatusCode)
            .withFailMessage("Expecting response status code to be %s but was %s", httpStatus, actualStatusCode)
            .isEqualTo(httpStatus);
        return this;
    }

    public ResponseEntityAssert<T> isOk() {
        return hasStatus(HttpStatus.OK);
    }

    public ResponseEntityAssert<T> isCreated() {
        return hasStatus(HttpStatus.CREATED);
    }

    public ResponseEntityAssert<T> isBadRequest() {
        return hasStatus(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntityAssert<T> isForbidden() {
        return hasStatus(HttpStatus.FORBIDDEN);
    }

    public ResponseEntityAssert<T> isInternalServerError() {
        return hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntityAssert<T> isUnauthorized() {
        return hasStatus(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntityAssert<T> isConflict() {
        return hasStatus(HttpStatus.CONFLICT);
    }

    /**
     * Verifies that the response contains the given object as body.
     *
     * @param body The expected body.
     * @return This instance.
     */
    public ResponseEntityAssert<T> hasBody(T body) {
        isNotNull();
        T actualBody = actual.getBody();
        Assertions.assertThat(actualBody)
            .withFailMessage("Expecting response entity body to be %s but was %s", body, actualBody)
            .isNotNull()
            .isEqualTo(body);
        return this;
    }
}
