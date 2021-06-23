package com.github.ngeor.yak4jcli;

/**
 * Helps with bumping a semantic version.
 */
public final class SemVerUtil {
    private SemVerUtil() {
    }

    /**
     * Bumps the given version.
     */
    public static String bump(String version, SemVerBump bump, boolean snapshot) {
        String[] parts = version.split("[.-]");
        String result;
        switch (bump) {
            case MAJOR:
                result = (Integer.parseInt(parts[0]) + 1) + ".0.0";
                break;
            case MINOR:
                result = parts[0] + "." + (Integer.parseInt(parts[1]) + 1) + ".0";
                break;
            case PATCH:
                result = parts[0] + "." + parts[1] + "." + (Integer.parseInt(parts[2]) + 1);
                break;
            default:
                throw new IllegalArgumentException("bump");
        }
        if (snapshot) {
            result += "-SNAPSHOT";
        }
        return result;
    }
}
