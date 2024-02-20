package com.github.ngeor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Release {
    private static final Pattern conventionalCommitPattern = Pattern.compile(
            "^(?<type>[a-z]+)(\\((?<scope>[a-z]+)\\))?(?<breaking>!)?:\\s*(?<description>.+)$",
            Pattern.CASE_INSENSITIVE);

    private final List<Group> groups;

    private Release(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> groups() {
        return groups;
    }

    public static Release create(Stream<Commit> commits) {
        List<Group> result = new ArrayList<>();
        for (var it = commits.iterator(); it.hasNext(); ) {
            Commit commit = it.next();
            CommitInfo.UntypedCommit untypedCommit = new CommitInfo.UntypedCommit(commit);
            if (result.isEmpty() || commit.tag() != null) {
                result.add(new Group(untypedCommit));
            } else {
                // addFirst, so that each group shows commits from the oldest to the newest
                result.getLast().addFirst(untypedCommit);
            }
        }
        return new Release(result);
    }

    public Release makeSubGroups(SubGroupOptions options) {
        return new Release(groups.stream().map(g -> g.makeSubGroups(options)).toList());
    }

    public record Group(CommitInfo tag, List<SubGroup> subGroups) {
        public Group(CommitInfo tag) {
            this(tag, Collections.singletonList(new SubGroup(tag)));
        }

        public void addFirst(CommitInfo commit) {
            subGroups.getLast().addFirst(commit);
        }

        public Group makeSubGroups(SubGroupOptions options) {
            if (subGroups.size() != 1) {
                throw new IllegalStateException("Already grouped");
            }

            return new Group(tag, subGroups.getFirst().makeSubGroups(options));
        }
    }

    public record SubGroup(String name, LinkedList<CommitInfo> commits) {

        public SubGroup(List<CommitInfo> commits) {
            this(null, new LinkedList<>(commits));
        }

        public SubGroup(CommitInfo... commits) {
            this(Arrays.asList(commits));
        }

        public void addFirst(CommitInfo commit) {
            commits.addFirst(commit);
        }

        public List<SubGroup> makeSubGroups(SubGroupOptions options) {
            if (name != null) {
                throw new IllegalStateException("Already grouped");
            }

            Map<String, Integer> orders = new HashMap<>();
            for (int i = 0; i < options.order().size(); i++) {
                orders.put(options.order().get(i), i);
            }

            Map<String, LinkedList<CommitInfo>> map = new TreeMap<>((o1, o2) -> {
                if (orders.containsKey(o1)) {
                    if (orders.containsKey(o2)) {
                        return orders.get(o1) - orders.get(o2);
                    } else {
                        return -1;
                    }
                } else {
                    if (orders.containsKey(o2)) {
                        return 1;
                    } else {
                        return o1.compareTo(o2);
                    }
                }
            });
            for (CommitInfo commit : commits) {
                if (new CommitFilter().test(commit.description())) {
                    CommitInfo conventionalCommit = toConventionalCommit(commit);
                    String type = Objects.requireNonNullElse(
                            options.scopeOverrider().apply(conventionalCommit), options.defaultGroup());
                    map.computeIfAbsent(type, ignored -> new LinkedList<>()).add(conventionalCommit);
                }
            }

            return map.entrySet().stream()
                    .map(e -> new SubGroup(e.getKey(), e.getValue()))
                    .toList();
        }

        private CommitInfo toConventionalCommit(CommitInfo commit) {
            Matcher matcher = conventionalCommitPattern.matcher(commit.description());
            if (matcher.matches()) {
                String type = matcher.group("type");
                String description = matcher.group("description");
                String scope = matcher.group("scope");
                boolean isBreaking = matcher.group("breaking") != null;
                return new CommitInfo.ConventionalCommit(
                        type, scope, description, isBreaking, commit.tag(), commit.authorDate());
            }

            return commit;
        }
    }

    public sealed interface CommitInfo {
        String type();

        String scope();

        String description();

        boolean isBreaking();

        String tag();

        LocalDate authorDate();

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

            @Override
            public String tag() {
                return commit.tag();
            }

            @Override
            public LocalDate authorDate() {
                return commit.authorDate();
            }
        }

        record ConventionalCommit(
                String type, String scope, String description, boolean isBreaking, String tag, LocalDate authorDate)
                implements CommitInfo {}
    }

    public record SubGroupOptions(
            String defaultGroup, List<String> order, Function<CommitInfo, String> scopeOverrider) {}
}
