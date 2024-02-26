package com.github.ngeor.changelog.group;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record Release(List<Group> groups) {
    public Release {
        Objects.requireNonNull(groups);
        Validate.noNullElements(groups);
        long numberOfGroupsWithoutTag =
                groups.stream().filter(UnreleasedGroup.class::isInstance).count();
        Validate.isTrue(numberOfGroupsWithoutTag <= 1, "Found more than one group without a tag");
    }

    public Release(Group... groups) {
        this(Arrays.asList(groups));
    }
}
