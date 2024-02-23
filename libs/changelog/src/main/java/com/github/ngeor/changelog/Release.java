package com.github.ngeor.changelog;

import com.github.ngeor.git.Commit;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public record Release(List<Group> groups) {
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
