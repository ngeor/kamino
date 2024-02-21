package com.github.ngeor.mr;

import com.github.ngeor.Tag;
import com.github.ngeor.versions.SemVer;

public final class TagPrefix {
    private TagPrefix() {}

    public static String tagPrefix(String path) {
        return validate(path) + "/v";
    }

    public static String tag(String path, SemVer version) {
        return validate(path) + "/v" + version;
    }

    public static SemVer version(String path, Tag tag) {
        return SemVer.parse(tag.name().substring(tagPrefix(path).length()));
    }

    private static String validate(String path) {
        if (path.isBlank() || path.endsWith("/") || path.endsWith("\\")) {
            throw new IllegalArgumentException();
        }

        return path;
    }
}
