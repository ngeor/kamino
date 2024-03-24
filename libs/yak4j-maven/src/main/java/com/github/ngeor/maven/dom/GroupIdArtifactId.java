package com.github.ngeor.maven.dom;

import org.apache.commons.lang3.StringUtils;

public record GroupIdArtifactId(String groupId, String artifactId) implements HasGroupIdArtifactId {
    @Override
    public String toString() {
        return String.format("%s:%s", groupId, artifactId);
    }

    public boolean hasMissingFields() {
        return StringUtils.isAnyBlank(groupId, artifactId);
    }

    public boolean isEmpty() {
        return groupId == null && artifactId == null;
    }
}
