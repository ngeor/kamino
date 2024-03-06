package com.github.ngeor.mr;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import java.util.Objects;

public record VersionDiff(MavenCoordinates oldVersion, SemVer newVersion) {
    public VersionDiff {
        Objects.requireNonNull(oldVersion);
        Objects.requireNonNull(newVersion);
        if (oldVersion.hasMissingFields()) {
            throw new IllegalArgumentException("Old version %s has missing fields".formatted(oldVersion));
        }
    }

    public VersionDiff toSnapshot() {
        return new VersionDiff(
                oldVersion.withVersion(newVersion.toString()),
                newVersion.bump(SemVerBump.MINOR).preRelease("SNAPSHOT"));
    }
}
