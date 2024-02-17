package com.github.ngeor;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

public class VersionResolver {
    private final GitTagProvider gitTagProvider;

    public VersionResolver(GitTagProvider gitTagProvider) {
        this.gitTagProvider = gitTagProvider;
    }

    public SemVer resolve(String version) throws IOException, InterruptedException {
        SortedSet<SemVer> versions = gitTagProvider.listVersions();
        SemVerBump bump = SemVerBump.parse(version);
        if (bump != null) {
            return versions.last().bump(bump);
        }

        SemVer result = SemVer.parse(version);
        if (!versions.isEmpty()) {
            if (versions.contains(result)) {
                throw new IllegalArgumentException(String.format("Version %s already exists", version));
            }

            SemVer latest = versions.last();
            Set<SemVer> allowed =
                    Set.of(latest.bump(SemVerBump.MAJOR), latest.bump(SemVerBump.MINOR), latest.bump(SemVerBump.PATCH));
            if (!allowed.contains(result)) {
                throw new IllegalArgumentException(
                        String.format("No sem ver gaps allowed. Allowed versions are: %s", allowed));
            }
        }

        return result;
    }
}
