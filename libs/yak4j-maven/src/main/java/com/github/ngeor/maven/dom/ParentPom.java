package com.github.ngeor.maven.dom;

import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record ParentPom(MavenCoordinates coordinates, String relativePath) {
    public MavenCoordinates validateCoordinates() {
        MavenCoordinates coordinates = Objects.requireNonNull(coordinates());
        Validate.notBlank(coordinates.groupId(), "groupId is missing from parent coordinates");
        Validate.notBlank(coordinates.artifactId(), "artifactId is missing from parent coordinates");
        Validate.notBlank(coordinates.version(), "version is missing from parent coordinates");
        return coordinates;
    }
}
