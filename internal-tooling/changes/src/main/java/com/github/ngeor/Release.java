package com.github.ngeor;

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
                result.add(new Group(commit));
            } else {
                // so that each group shows commits from the oldest to the newest
                result.getLast().addFirst(commit);
            }
        }
        return new Release(result);
    }

    public Release filter(Predicate<Commit> predicate) {
        return new Release(groups.stream().map(g -> g.filter(predicate)).toList());
    }

    public Release makeSubGroups(SubGroupOptions options) {
        return new Release(groups.stream().map(g -> g.makeSubGroups(options)).toList());
    }

    public record Group(Commit tag, List<SubGroup> subGroups) {
        public Group(Commit tag) {
            this(tag, Collections.singletonList(new SubGroup(tag)));
        }

        public Group(List<Commit> commits) {
            this(commits.getFirst(), Collections.singletonList(new SubGroup(commits.reversed())));
        }

        public void addFirst(Commit commit) {
            subGroups.getLast().addFirst(commit);
        }

        public Group filter(Predicate<Commit> predicate) {
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

    public record SubGroup(String name, LinkedList<Commit> commits) {
        public SubGroup(List<Commit> commits) {
            this(null, new LinkedList<>(commits));
        }

        public SubGroup(Commit... commits) {
            this(Arrays.asList(commits));
        }

        public void addFirst(Commit commit) {
            commits.addFirst(commit);
        }

        public SubGroup filter(Predicate<Commit> predicate) {
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

            Map<String, LinkedList<Commit>> map = new TreeMap<>((o1, o2) -> {
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
            for (Commit commit : commits) {
                String summary = commit.summary();
                String[] parts = summary.split(":", 2);
                String prefix = parts.length == 2 ? parts[0] : options.defaultGroup();
                map.computeIfAbsent(prefix, ignored -> new LinkedList<>()).add(commit);
            }

            return map.entrySet().stream()
                    .map(e -> new SubGroup(e.getKey(), e.getValue()))
                    .toList();
        }
    }

    public record SubGroupOptions(String defaultGroup, List<String> order) {}
}
