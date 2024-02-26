package com.github.ngeor.changelog.group;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record SubGroup(String name, List<CommitInfo> commits) {
    public SubGroup {
        Validate.notBlank(name);
        Objects.requireNonNull(commits);
    }

    public SubGroup(String name, CommitInfo... commits) {
        this(name, Arrays.asList(commits));
    }
}
