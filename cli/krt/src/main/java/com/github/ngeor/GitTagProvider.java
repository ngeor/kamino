package com.github.ngeor;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GitTagProvider {
    private final Git git;
    private final GitTagPrefix gitTagPrefix;

    public GitTagProvider(Git git, GitTagPrefix gitTagPrefix) {
        this.git = git;
        this.gitTagPrefix = gitTagPrefix;
    }

    public SortedSet<SemVer> listVersions() throws IOException, InterruptedException {
        String tagPrefix = gitTagPrefix.getPrefix();
        String tagPattern = tagPrefix + "*";
        List<String> tags = git.listTags(tagPattern);
        return tags.stream()
                .map(tag -> tag.substring(tagPrefix.length()))
                .map(SemVer::parse)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
