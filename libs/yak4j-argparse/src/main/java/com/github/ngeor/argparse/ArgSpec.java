package com.github.ngeor.argparse;

import java.util.Objects;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.Validate;

public record ArgSpec(
        String name, boolean required, SpecKind kind, String description, UnaryOperator<String> normalizer) {

    public ArgSpec {
        Validate.notBlank(name);
        Objects.requireNonNull(kind);
    }

    public String normalize(String value) {
        return normalizer != null ? normalizer.apply(value) : value;
    }
}
