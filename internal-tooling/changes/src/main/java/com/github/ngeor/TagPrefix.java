package com.github.ngeor;

public final class TagPrefix {
    private TagPrefix() {}

    public static String tagPrefix(String path) {
        return path + "/v";
    }
}
