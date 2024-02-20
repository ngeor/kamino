package com.github.ngeor.argparse;

import java.util.function.UnaryOperator;

public class ArgSpecBuilder {
    private String name;
    private boolean required;
    private SpecKind kind;
    private String description;
    private UnaryOperator<String> normalizer;

    public ArgSpecBuilder(String name, SpecKind kind) {
        this.name = name;
        this.kind = kind;
    }

    public ArgSpecBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ArgSpecBuilder required() {
        return required(true);
    }

    public ArgSpecBuilder required(boolean required) {
        this.required = required;
        return this;
    }

    public ArgSpecBuilder kind(SpecKind kind) {
        this.kind = kind;
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
