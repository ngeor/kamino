package com.github.ngeor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ReleaseGrouper(Release.SubGroupOptions options) {
    private static final Pattern conventionalCommitPattern = Pattern.compile(
            "^(?<type>[a-z]+)(\\((?<scope>[a-z]+)\\))?(?<breaking>!)?:\\s*(?<description>.+)$",
            Pattern.CASE_INSENSITIVE);

    public Release toRelease(Stream<Commit> commits) {
        return fromCommitGroups(fromCommits(commits));
    }

    public List<List<Commit>> fromCommits(Stream<Commit> commits) {
        return commits.reduce(
                Collections.emptyList(),
                (listOfLists, commit) -> {
                    if (listOfLists.isEmpty()) {
                        return Collections.singletonList(Collections.singletonList(commit));
                    }

                    LinkedList<List<Commit>> result = new LinkedList<>(listOfLists);
                    if (commit.tag() != null) {
                        result.add(Collections.singletonList(commit));
                    } else {
                        LinkedList<Commit> lastGroup = new LinkedList<>(result.removeLast());
                        lastGroup.addFirst(commit);
                        result.add(lastGroup);
                    }

                    return result;
                },
                (x, y) -> {
                    List<List<Commit>> result = new ArrayList<>(x);
                    result.addAll(y);
                    return result;
                });
    }

    public Release fromCommitGroups(List<List<Commit>> groups) {
        return new Release(groupsFromCommitGroups(groups));
    }

    private List<Release.Group> groupsFromCommitGroups(List<List<Commit>> groups) {
        return groups.stream().flatMap(g -> groupFromCommitGroup(g).stream()).toList();
    }

    private Optional<Release.Group> groupFromCommitGroup(List<Commit> commitGroup) {
        List<Release.CommitInfo> commitInfos = commitGroup.stream()
                // filter out excluded commits
                .filter(c -> new CommitFilter().test(c.summary()))
                // resolve type, scope, etc
                .map(this::toCommitInfo)
                .toList();
        // the tag, if it exists, will be on this commit
        Commit lastCommit = commitGroup.get(commitGroup.size() - 1);
        if (commitInfos.isEmpty() && lastCommit.tag() == null) {
            // do not render "Unreleased" section if it is empty
            return Optional.empty();
        }

        // group them by "type", sorted according to options
        Map<String, List<Release.CommitInfo>> groupedByType = commitInfos.stream()
                .collect(Collectors.groupingBy(this::calculateType, this::createTreeMap, Collectors.toList()));

        return Optional.of(new Release.Group(
                lastCommit,
                groupedByType.entrySet().stream()
                        .map(e -> new Release.SubGroup(e.getKey(), e.getValue()))
                        .toList()));
    }

    private String calculateType(Release.CommitInfo commitInfo) {
        // override the type if needed, fallback to the default group if null
        return Objects.requireNonNullElseGet(options.scopeOverrider().apply(commitInfo), options::defaultGroup);
    }

    private Map<String, List<Release.CommitInfo>> createTreeMap() {
        Map<String, Integer> orders = new HashMap<>();
        for (int i = 0; i < options.order().size(); i++) {
            orders.put(options.order().get(i), i);
        }

        return new TreeMap<>((o1, o2) -> {
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
    }

    private Release.CommitInfo toCommitInfo(Commit commit) {
        Matcher matcher = conventionalCommitPattern.matcher(commit.summary());
        if (matcher.matches()) {
            String type = matcher.group("type");
            String description = matcher.group("description");
            String scope = matcher.group("scope");
            boolean isBreaking = matcher.group("breaking") != null;
            return new Release.CommitInfo.ConventionalCommit(type, scope, description, isBreaking);
        }

        return new Release.CommitInfo.UntypedCommit(commit);
    }
}
