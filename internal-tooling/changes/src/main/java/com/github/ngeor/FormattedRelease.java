package com.github.ngeor;

import java.util.List;

public record FormattedRelease(List<Group> groups) {
    public record Group(String title, List<SubGroup> subGroups) {}

    public record SubGroup(String title, List<String> items) {}
}
