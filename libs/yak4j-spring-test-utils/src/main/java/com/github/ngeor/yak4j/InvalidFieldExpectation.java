package com.github.ngeor.yak4j;

/**
 * An expectation about an invalid field.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class InvalidFieldExpectation {
    private final String field;
    private final String code;

    InvalidFieldExpectation(String field, String code) {
        this.field = field;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }
}
