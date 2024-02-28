package com.github.ngeor.mr;

import com.github.ngeor.changelog.format.FormatOptions;
import java.util.Map;

public final class Defaults {
    private Defaults() {}

    public static final String XML_INDENTATION = "  ";

    public static FormatOptions defaultFormatOptions() {
        return new FormatOptions(
                "Unreleased",
                Map.of("feat", "Features", "fix", "Fixes", "chore", "Miscellaneous Tasks", "deps", "Dependencies"),
                "https://github.com/ngeor/kamino/compare/%s...%s");
    }

    public static boolean isEligibleForRelease(String module) {
        return module != null
                && (module.startsWith("archetypes/")
                        || module.startsWith("libs/")
                        || module.startsWith("plugins/")
                        || module.startsWith("poms/"));
    }
}
