package com.github.ngeor.changelog.format;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record FormattedSubGroup(String title, List<String> items) {
    public FormattedSubGroup {
        Validate.notBlank(title);
        Objects.requireNonNull(items);
    }

    public FormattedSubGroup(String title, String... items) {
        this(title, Arrays.asList(items));
    }
}
