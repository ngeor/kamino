package com.github.ngeor.yak4jcli;

/**
 * Helps with bumping a semantic version.
 */
public final class SemVerUtil {
    private SemVerUtil() {
    }

    public static String bump(String version) {
        String[] parts = version.split("[.-]");
        return parts[0] + "." + (Integer.parseInt(parts[1]) + 1) + ".0";
    }
}
