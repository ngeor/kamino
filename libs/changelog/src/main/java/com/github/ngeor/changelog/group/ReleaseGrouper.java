package com.github.ngeor.changelog.group;

import com.github.ngeor.changelog.CommitFilter;
import com.github.ngeor.git.Commit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ReleaseGrouper(SubGroupOptions options) {
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

    private List<Group> groupsFromCommitGroups(List<List<Commit>> groups) {
        return groups.stream().flatMap(g -> groupFromCommitGroup(g).stream()).toList();
    }

    private Optional<Group> groupFromCommitGroup(List<Commit> commitGroup) {
        List<CommitInfo> commitInfos = commitGroup.stream()
                // filter out excluded commits
                .filter(c -> new CommitFilter().test(c.summary()))
                // resolve type, scope, etc
                .map(CommitInfoFactory::toCommitInfo)
                .toList();
        // the tag, if it exists, will be on this commit
        Commit lastCommit = commitGroup.get(commitGroup.size() - 1);
        if (commitInfos.isEmpty() && lastCommit.tag() == null) {
            // do not render "Unreleased" section if it is empty
            return Optional.empty();
        }

        // group them by "type", sorted according to options
        Map<String, List<CommitInfo>> groupedByType = commitInfos.stream()
                .collect(Collectors.groupingBy(this::calculateType, this::createTreeMap, Collectors.toList()));

        List<SubGroup> subGroups = groupedByType.entrySet().stream()
                .map(e -> new SubGroup(e.getKey(), e.getValue()))
                .toList();
        return Optional.of(
                lastCommit.tag() != null
                        ? new TaggedGroup(lastCommit, subGroups)
                        : new UnreleasedGroup(subGroups));
    }

    private String calculateType(CommitInfo commitInfo) {
        // override the type if needed, fallback to the default group if null
        return Objects.requireNonNullElseGet(options.typeOverrider().apply(commitInfo), options::defaultType);
    }

    private Map<String, List<CommitInfo>> createTreeMap() {
        Map<String, Integer> orders = new HashMap<>();
        for (int i = 0; i < options.typeOrder().size(); i++) {
            orders.put(options.typeOrder().get(i), i);
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
}
