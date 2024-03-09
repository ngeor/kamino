package com.github.ngeor.maven.dom;

import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record ParentPom(MavenCoordinates coordinates, String relativePath) implements HasGroupIdArtifactId, HasVersion {
    public ParentPom {
        Objects.requireNonNull(coordinates);
    }

    public ParentPom(String groupId, String artifactId, String version, String relativePath) {
        this(new MavenCoordinates(groupId, artifactId, version), relativePath);
    }

    public MavenCoordinates validateCoordinates() {
        Validate.notBlank(groupId(), "groupId is missing from parent coordinates");
        Validate.notBlank(artifactId(), "artifactId is missing from parent coordinates");
        Validate.notBlank(version(), "version is missing from parent coordinates");
        return coordinates;
    }

    @Override
    public String groupId() {
        return coordinates.groupId();
    }

    @Override
    public String artifactId() {
        return coordinates.artifactId();
    }

    @Override
    public String version() {
        return coordinates.version();
    }
}
