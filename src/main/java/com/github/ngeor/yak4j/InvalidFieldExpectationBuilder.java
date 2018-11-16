package com.github.ngeor.yak4j;

/**
 * Builds an {@link InvalidFieldExpectation}.
 */
@SuppressWarnings("unused")
public final class InvalidFieldExpectationBuilder {
    private String field;

    private InvalidFieldExpectationBuilder(String field) {
        this.field = field;
    }

    public InvalidFieldExpectation withCode(String code) {
        return new InvalidFieldExpectation(field, code);
    }

    /**
     * Creates a new builder for an {@link InvalidFieldExpectation}.
     * @param field The name of the field that is expected to be invalid.
     * @return A new builder.
     */
    public static InvalidFieldExpectationBuilder invalidField(String field) {
        return new InvalidFieldExpectationBuilder(field);
    }
}
