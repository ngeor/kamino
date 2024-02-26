package com.github.ngeor.changelog.format;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record FormattedGroup(String title, List<FormattedSubGroup> subGroups) {
    public FormattedGroup {
        Validate.notBlank(title);
        Objects.requireNonNull(subGroups);
    }

    public FormattedGroup(String title, FormattedSubGroup... subGroups) {
        this(title, Arrays.asList(subGroups));
    }
}
