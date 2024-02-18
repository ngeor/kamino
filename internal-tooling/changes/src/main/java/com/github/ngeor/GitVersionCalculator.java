package com.github.ngeor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GitVersionCalculator {
    private final Git git;
    private final String path;
    private final String tagPrefix;

    public GitVersionCalculator(Git git, String path) {
        this.git = git;
        this.path = path;
        this.tagPrefix = TagPrefix.tagPrefix(path);
    }

    public Optional<Result> calculateGitVersion() throws IOException, InterruptedException, ProcessFailedException {
        SemVer mostRecentVersion = git.getMostRecentTag(tagPrefix)
                .map(tag -> TagPrefix.version(path, tag))
                .orElse(null);
        return mostRecentVersion != null ? Optional.of(calculateGitVersion(mostRecentVersion)) : Optional.empty();
    }

    private Result calculateGitVersion(SemVer mostRecentVersion)
            throws IOException, ProcessFailedException, InterruptedException {
        String sinceCommit = TagPrefix.tag(path, mostRecentVersion);

        List<Commit> commits = git.revList(sinceCommit, path).toList();
        if (commits.isEmpty()) {
            // definitely no commits
            return new Result(mostRecentVersion, null, null);
        }

        SemVerBump bump = commits.stream()
                .filter(new CommitFilter())
                .map(Commit::summary)
                .map(this::calculateBump)
                .max(Enum::compareTo)
                .orElse(null);

        if (bump == null) {
            // no commits after filtering, so we could return here a "MINOR" to allow tagging even though there are no
            // relevant commits
            return new Result(mostRecentVersion, null, null);
        }

        SemVer nextVersion = mostRecentVersion.bump(bump);
        return new Result(mostRecentVersion, bump, nextVersion);
    }

    private SemVerBump calculateBump(String message) {
        if (message.startsWith("fix:")) {
            return SemVerBump.PATCH;
        }

        if (message.matches("^[a-z]+!:.+$")) {
            return SemVerBump.MAJOR;
        }

        return SemVerBump.MINOR;
    }

    public record Result(SemVer mostRecentVersion, SemVerBump bump, SemVer nextVersion) {}
}
