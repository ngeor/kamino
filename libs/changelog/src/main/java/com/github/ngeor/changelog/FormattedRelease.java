package com.github.ngeor.changelog;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record FormattedRelease(List<Group> groups) {
    public FormattedRelease {
        Objects.requireNonNull(groups);
    }

    public FormattedRelease(Group... groups) {
        this(Arrays.asList(groups));
    }

    public record Group(String title, List<SubGroup> subGroups) {
        public Group {
            Validate.notBlank(title);
            Objects.requireNonNull(subGroups);
        }

        public Group(String title, SubGroup... subGroups) {
            this(title, Arrays.asList(subGroups));
        }
    }

    public record SubGroup(String title, List<String> items) {
        public SubGroup {
            Validate.notBlank(title);
            Objects.requireNonNull(items);
        }

        public SubGroup(String title, String... items) {
            this(title, Arrays.asList(items));
        }
    }
}
