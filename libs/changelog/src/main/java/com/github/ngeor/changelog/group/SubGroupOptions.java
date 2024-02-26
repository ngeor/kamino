package com.github.ngeor.changelog.group;

import java.util.List;
import java.util.function.Function;

public record SubGroupOptions(
    String defaultGroup, List<String> order, Function<CommitInfo, String> scopeOverrider) {
}
