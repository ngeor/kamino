package com.github.ngeor.argparse;

import java.util.Objects;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.Validate;

public class ArgSpecBuilder {
    private final String name;
    private final SpecKind kind;
    private boolean required;
    private String description;
    private UnaryOperator<String> normalizer;

    public ArgSpecBuilder(String name, SpecKind kind) {
        this.name = Validate.notBlank(name);
        this.kind = Objects.requireNonNull(kind);
    }

    public ArgSpecBuilder required() {
        return required(true);
    }

    public ArgSpecBuilder required(boolean required) {
        this.required = required;
        return this;
    }

    public ArgSpecBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ArgSpecBuilder normalizer(UnaryOperator<String> normalizer) {
        this.normalizer = normalizer;
        return this;
    }

    public ArgSpec build() {
        return new ArgSpec(name, required, kind, description, normalizer);
    }
}
