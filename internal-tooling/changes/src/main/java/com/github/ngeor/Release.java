package com.github.ngeor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Release(List<Group> groups) {

    public static Release create(Stream<Commit> commits) {
        List<Group> result = new ArrayList<>();
        for (var it = commits.iterator(); it.hasNext(); ) {
            Commit commit = it.next();
            if (result.isEmpty() || commit.tag() != null) {
                result.add(new Group(CommitInfo.fromCommit(commit)));
            } else {
                // addFirst, so that each group shows commits from the oldest to the newest
                result.getLast().addFirst(CommitInfo.fromCommit(commit));
            }
        }
        return new Release(result);
    }

    public Release filter(Predicate<CommitInfo> predicate) {
        return new Release(groups.stream().map(g -> g.filter(predicate)).toList());
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

        public Group filter(Predicate<CommitInfo> predicate) {
            return new Group(
                    tag, subGroups.stream().map(g -> g.filter(predicate)).toList());
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

        public SubGroup filter(Predicate<CommitInfo> predicate) {
            return new SubGroup(
                    name, commits.stream().filter(predicate).collect(Collectors.toCollection(LinkedList::new)));
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
                CommitInfo n = commit.regroup(options);
                String prefix = n.group();
                map.computeIfAbsent(prefix, ignored -> new LinkedList<>()).add(n);
            }

            return map.entrySet().stream()
                    .map(e -> new SubGroup(e.getKey(), e.getValue()))
                    .toList();
        }
    }

    public record CommitInfo(Commit commit, String group, boolean isBreaking) {
        public static CommitInfo fromCommit(Commit commit) {
            return new CommitInfo(commit, null, false);
        }

        public CommitInfo regroup(SubGroupOptions options) {
            String summary = commit.summary();
            String[] parts = summary.split(":", 2);
            String prefix = parts.length == 2 ? parts[0] : options.defaultGroup();
            boolean isBreaking = prefix.endsWith("!");
            if (isBreaking) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            return new CommitInfo(commit, prefix, isBreaking);
        }

        public String summary() {
            return commit.summary();
        }

        public String summaryWithoutPrefix() {
            return Arrays.asList(summary().split(":", 2)).reversed().getFirst().trim();
        }

        public String tag() {
            return commit.tag();
        }

        public LocalDate authorDate() {
            return commit.authorDate();
        }
    }

    public record SubGroupOptions(String defaultGroup, List<String> order) {}
}
