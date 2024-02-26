package com.github.ngeor.mr;

import com.github.ngeor.changelog.FormatOptions;
import java.util.Map;

public final class Defaults {
    private Defaults() {}

    public static FormatOptions defaultFormatOptions() {
        return new FormatOptions(
                "Unreleased",
                Map.of("feat", "Features", "fix", "Fixes", "chore", "Miscellaneous Tasks", "deps", "Dependencies"),
                "https://github.com/ngeor/kamino/compare/%s...%s");
    }
}
