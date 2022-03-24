package com.github.ngeor;

import java.nio.file.Path;

public class GitTagMessageProvider {
    private final Path currentDir;
    private final Path projectDir;
    private final String version;

    public GitTagMessageProvider(Path currentDir, Path projectDir, String version) {
        this.currentDir = currentDir;
        this.projectDir = projectDir;
        this.version = version;
    }

    public String getMessage() {
        if (currentDir.equals(projectDir)) {
            return "Releasing version " + version;
        }

        return "Releasing version " + version + " of " + currentDir.getFileName().toString();
    }

    public String getTag() {
        if (currentDir.equals(projectDir)) {
            return "v" + version;
        }
        return currentDir.getFileName().toString() + "/" + version;
    }
}
