package com.github.ngeor.maven;

import org.apache.commons.lang3.StringUtils;

public record MavenCoordinates(String groupId, String artifactId, String version) {

    public boolean hasMissingFields() {
        return StringUtils.isAnyBlank(groupId, artifactId, version);
    }

    public boolean isValid() {
        return !hasMissingFields();
    }

    public MavenCoordinates withVersion(String newVersion) {
        return new MavenCoordinates(groupId, artifactId, newVersion);
    }

    public String format() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }

    @Override
    public String toString() {
        return String.format("%s %s", MavenCoordinates.class.getSimpleName(), format());
    }
}
