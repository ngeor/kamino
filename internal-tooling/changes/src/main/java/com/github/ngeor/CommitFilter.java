package com.github.ngeor;

import java.util.List;
import java.util.function.Predicate;

public class CommitFilter implements Predicate<Commit> {
    private final List<String> needles = List.of("maven-release-plugin", "changelog");

    @Override
    public boolean test(Commit commit) {
        return needles.stream().noneMatch(needle -> commit.summary().contains(needle));
    }
}
