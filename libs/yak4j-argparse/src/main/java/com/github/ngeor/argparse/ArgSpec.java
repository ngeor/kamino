package com.github.ngeor.argparse;

import java.util.function.UnaryOperator;

public record ArgSpec(
        String name, boolean required, SpecKind kind, String description, UnaryOperator<String> normalizer) {
    public String normalize(String value) {
        return normalizer != null ? normalizer.apply(value) : value;
    }
}
