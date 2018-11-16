package com.github.ngeor.yak4j;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Entry point for assertion DSL.
 */
@SuppressWarnings("WeakerAccess")
public final class Assertions {
    private Assertions() {
    }

    public static ResultActionsAssert assertThat(ResultActions resultActions) {
        return new ResultActionsAssert(resultActions);
    }

    public static <T> ResponseEntityAssert<T> assertThat(ResponseEntity<T> responseEntity) {
        return new ResponseEntityAssert<>(responseEntity);
    }
}
