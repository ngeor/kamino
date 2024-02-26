package com.github.ngeor.changelog.group;

import com.github.ngeor.git.Commit;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record TaggedGroup(Commit mostRecentCommit, List<SubGroup> subGroups) implements Group {
    public TaggedGroup {
        Objects.requireNonNull(mostRecentCommit);
        Objects.requireNonNull(mostRecentCommit.tag());
        Objects.requireNonNull(mostRecentCommit.authorDate());
    }

    public TaggedGroup(Commit mostRecentCommit, SubGroup... subGroups) {
        this(mostRecentCommit, Arrays.asList(subGroups));
    }

    public String tag() {
        return mostRecentCommit.tag();
    }

    public LocalDate authorDate() {
        return mostRecentCommit.authorDate();
    }
}
