package com.github.ngeor.versions;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum SemVerBump {
    PATCH,
    MINOR,
    MAJOR;

    public static SemVerBump parse(String value) {
        Objects.requireNonNull(value);
        return tryParse(value)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unknown %s value %s", SemVerBump.class.getSimpleName(), value)));
    }

    public static Optional<SemVerBump> tryParse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(v -> v.toString().equalsIgnoreCase(value))
                .findFirst();
    }
}
