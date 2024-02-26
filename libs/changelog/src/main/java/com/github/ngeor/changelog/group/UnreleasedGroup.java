package com.github.ngeor.changelog.group;

import java.util.Arrays;
import java.util.List;

public record UnreleasedGroup(List<SubGroup> subGroups) implements Group {
    public UnreleasedGroup(SubGroup... subGroups) {
        this(Arrays.asList(subGroups));
    }
}
