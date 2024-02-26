package com.github.ngeor.changelog.group;

import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Commit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public record CommitGrouper(TagPrefix tagPrefix) {
    public List<List<Commit>> fromCommits(Stream<Commit> commits) {
        return commits.reduce(Collections.emptyList(), this::addCommit, this::addLists);
    }

    private List<List<Commit>> addCommit(List<List<Commit>> listOfLists, Commit commit) {
        if (listOfLists.isEmpty()) {
            return Collections.singletonList(Collections.singletonList(commit));
        }

        LinkedList<List<Commit>> result = new LinkedList<>(listOfLists);
        if (tagPrefix.tagStartsWithExpectedPrefix(commit.tag())) {
            // start new group
            result.add(Collections.singletonList(commit));
        } else {
            // pop last group out of result, make it mutable
            LinkedList<Commit> lastGroup = new LinkedList<>(result.removeLast());
            // prepend the commit (so that oldest commits appear first)
            lastGroup.addFirst(commit);
            // re-add the group to the result
            result.add(lastGroup);
        }

        return result;
    }

    private <E> List<E> addLists(List<E> left, List<E> right) {
        List<E> result = new ArrayList<>(left.size() + right.size());
        result.addAll(left);
        result.addAll(result);
        return result;
    }
}
