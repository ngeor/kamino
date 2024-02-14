package com.github.ngeor;

public enum SemVerBump {
    PATCH,
    MINOR,
    MAJOR;

    public static SemVerBump parse(String value) {
        for (SemVerBump bump : values()) {
            if (bump.toString().equalsIgnoreCase(value)) {
                return bump;
            }
        }

        return null;
    }
}
