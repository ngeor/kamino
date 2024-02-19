package com.github.ngeor;

import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GitTagProvider implements VersionsProvider {
    private final Git git;
    private final Supplier<String> gitTagPrefix;

    public GitTagProvider(Git git, Supplier<String> gitTagPrefix) {
        this.git = git;
        this.gitTagPrefix = gitTagPrefix;
    }

    @Override
    public SortedSet<SemVer> listVersions() throws IOException, InterruptedException {
        String tagPrefix = gitTagPrefix.get();
        String tagPattern = tagPrefix + "*";
        List<String> tags = git.listTags(tagPattern);
        return tags.stream()
                .map(tag -> tag.substring(tagPrefix.length()))
                .map(SemVer::parse)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
