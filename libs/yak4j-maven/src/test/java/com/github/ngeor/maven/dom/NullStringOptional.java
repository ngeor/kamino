package com.github.ngeor.maven.dom;

import java.util.Optional;

public record NullStringOptional(String value, boolean isPresent) {
    public static NullStringOptional empty() {
        return new NullStringOptional(null, false);
    }

    public Optional<String> toOptional() {
        return Optional.ofNullable(value);
    }

    public boolean isEmpty() {
        return !isPresent;
    }
}
