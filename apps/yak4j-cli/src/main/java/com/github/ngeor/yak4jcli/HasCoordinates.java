package com.github.ngeor.yak4jcli;

import java.util.Objects;

/**
 * Describes an entity with Maven coordinates.
 */
public interface HasCoordinates {
    String getGroupId();

    String getArtifactId();

    default boolean matchesGroupArtifact(HasCoordinates other) {
        return other != null && Objects.equals(this.getGroupId(), other.getGroupId())
            && Objects.equals(this.getArtifactId(), other.getArtifactId());
    }
}
