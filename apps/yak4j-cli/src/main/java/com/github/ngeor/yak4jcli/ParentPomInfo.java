package com.github.ngeor.yak4jcli;

import java.nio.file.Path;

/**
 * A POJO containing information of a parent pom.
 */
class ParentPomInfo {
    private final Path path;
    private final String groupId;
    private final String artifactId;
    private final String version;

    /**
     * Creates an instance of this class.
     */
    ParentPomInfo(Path path, String groupId, String artifactId, String version) {
        this.path = path;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Path getPath() {
        return path;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }
}
