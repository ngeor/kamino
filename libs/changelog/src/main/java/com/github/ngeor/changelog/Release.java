package com.github.ngeor.changelog;

import com.github.ngeor.git.Commit;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.lang3.Validate;

public record Release(List<Group> groups) {
    public Release {
        Objects.requireNonNull(groups);
        Validate.noNullElements(groups);
        long numberOfGroupsWithoutTag =
                groups.stream().filter(UnreleasedGroup.class::isInstance).count();
        Validate.isTrue(numberOfGroupsWithoutTag <= 1, "Found more than one group without a tag");
    }

    public sealed interface Group permits UnreleasedGroup, TaggedGroup {
        List<SubGroup> subGroups();
    }

    public record UnreleasedGroup(List<SubGroup> subGroups) implements Group {}

    public record TaggedGroup(Commit mostRecentCommit, List<SubGroup> subGroups) implements Group {
        public TaggedGroup {
            Objects.requireNonNull(mostRecentCommit);
            Objects.requireNonNull(mostRecentCommit.tag());
            Objects.requireNonNull(mostRecentCommit.authorDate());
        }

        public String tag() {
            return mostRecentCommit.tag();
        }

        public LocalDate authorDate() {
            return mostRecentCommit.authorDate();
        }
    }

    public record SubGroup(String name, List<CommitInfo> commits) {
        public SubGroup {
            Validate.notBlank(name);
        }
    }

    public sealed interface CommitInfo {
        String type();

        String scope();

        String description();

        boolean isBreaking();

        record UntypedCommit(Commit commit) implements CommitInfo {
            @Override
            public String type() {
                return null;
            }

            @Override
            public String scope() {
                return null;
            }

            @Override
            public String description() {
                return commit.summary();
            }

            @Override
            public boolean isBreaking() {
                return false;
            }
        }

        record ConventionalCommit(String type, String scope, String description, boolean isBreaking)
                implements CommitInfo {}
    }

    public record SubGroupOptions(
            String defaultGroup, List<String> order, Function<CommitInfo, String> scopeOverrider) {}
}
