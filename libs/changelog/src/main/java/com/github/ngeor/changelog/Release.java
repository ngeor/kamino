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
        long numberOfGroupsWithoutTag =
                groups.stream().filter(g -> g.tag() == null).count();
        Validate.isTrue(numberOfGroupsWithoutTag <= 1, "Found more than one group without a tag");
    }

    public record Group(Commit mostRecentCommit, List<SubGroup> subGroups) {
        public String tag() {
            return mostRecentCommit.tag();
        }

        public LocalDate authorDate() {
            return mostRecentCommit.authorDate();
        }
    }

    public record SubGroup(String name, List<CommitInfo> commits) {}

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
