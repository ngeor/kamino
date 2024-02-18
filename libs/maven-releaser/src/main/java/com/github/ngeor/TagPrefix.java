package com.github.ngeor;

public final class TagPrefix {
    private TagPrefix() {}

    public static String tagPrefix(String path) {
        return validate(path) + "/v";
    }

    public static String tag(String path, SemVer version) {
        return validate(path) + "/v" + version;
    }

    private static String validate(String path) {
        if (path.isBlank() || path.endsWith("/") || path.endsWith("\\")) {
            throw new IllegalArgumentException();
        }

        return path;
    }
}
