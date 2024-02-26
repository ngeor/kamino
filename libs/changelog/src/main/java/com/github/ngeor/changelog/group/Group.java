package com.github.ngeor.changelog.group;

import java.util.List;

public sealed interface Group permits UnreleasedGroup, TaggedGroup {
    List<SubGroup> subGroups();
}
