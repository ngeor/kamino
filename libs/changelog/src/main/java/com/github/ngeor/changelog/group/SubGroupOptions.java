package com.github.ngeor.changelog.group;

import java.util.List;
import java.util.function.Function;

public record SubGroupOptions(
    String defaultType, List<String> typeOrder, Function<CommitInfo, String> typeOverrider) {
}
