package com.github.ngeor.changelog.format;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record FormattedRelease(List<FormattedGroup> groups) {
    public FormattedRelease {
        Objects.requireNonNull(groups);
    }

    public FormattedRelease(FormattedGroup... groups) {
        this(Arrays.asList(groups));
    }
}
