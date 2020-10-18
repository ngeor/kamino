package com.github.ngeor.yak4j;

/**
 * Utility methods.
 */
final class Util {
    private Util() {}

    /**
     * Converts a version to a tag.
     * The version does not have a prefix (e.g. 1.2.3), but
     * the tag has (e.g. v1.2.3).
     */
    static String versionToTag(String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("version cannot be empty");
        }

        return "v" + version;
    }
}
