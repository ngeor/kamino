package com.github.ngeor.changelog.group;

import com.github.ngeor.git.Commit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class CommitGrouper {
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
}
