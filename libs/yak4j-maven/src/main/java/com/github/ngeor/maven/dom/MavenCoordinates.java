package com.github.ngeor.maven.dom;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public record MavenCoordinates(GroupIdArtifactId groupIdArtifactId, String version)
        implements HasGroupIdArtifactId, HasVersion {
    public MavenCoordinates {
        Objects.requireNonNull(groupIdArtifactId);
    }

    public MavenCoordinates(String groupId, String artifactId, String version) {
        this(new GroupIdArtifactId(groupId, artifactId), version);
    }

    public boolean hasMissingFields() {
        return groupIdArtifactId.hasMissingFields() || StringUtils.isBlank(version);
    }

    public MavenCoordinates withVersion(String newVersion) {
        return new MavenCoordinates(groupIdArtifactId, newVersion);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", groupIdArtifactId, version);
    }

    @Override
    public String groupId() {
        return groupIdArtifactId.groupId();
    }

    @Override
    public String artifactId() {
        return groupIdArtifactId.artifactId();
    }
}
