package com.github.ngeor;

import com.github.ngeor.changelog.CommitFilter;
import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import java.util.List;
import java.util.Optional;

public class GitVersionCalculator {
    private final Git git;
    private final String path;
    private final TagPrefix tagPrefix;

    public GitVersionCalculator(Git git, String path) {
        this.git = git;
        this.path = path;
        this.tagPrefix = TagPrefix.forPath(path);
    }

    public Optional<Result> calculateGitVersion() throws ProcessFailedException {
        SemVer mostRecentVersion = git.getMostRecentTag(tagPrefix.tagPrefix())
                .map(tagPrefix::stripTagPrefix)
                .orElse(null);
        return mostRecentVersion != null ? Optional.of(calculateGitVersion(mostRecentVersion)) : Optional.empty();
    }

    private Result calculateGitVersion(SemVer mostRecentVersion) throws ProcessFailedException {
        String sinceCommit = tagPrefix.addTagPrefix(mostRecentVersion);

        List<Commit> commits = git.revList(sinceCommit, path).toList();
        if (commits.isEmpty()) {
            // definitely no commits
            return new Result(mostRecentVersion, null, null);
        }

        SemVerBump bump = commits.stream()
                .map(Commit::summary)
                .filter(new CommitFilter())
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
