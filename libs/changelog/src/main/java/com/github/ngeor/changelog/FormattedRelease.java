package com.github.ngeor.changelog;

import java.util.List;
import org.apache.commons.lang3.Validate;

public record FormattedRelease(List<Group> groups) {
    public record Group(String title, List<SubGroup> subGroups) {
        public Group {
            Validate.notBlank(title);
        }
    }

    public record SubGroup(String title, List<String> items) {
        public SubGroup {
            Validate.notBlank(title);
        }
    }
}
