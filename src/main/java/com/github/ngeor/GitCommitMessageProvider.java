package com.github.ngeor;

import java.nio.file.Path;

public class GitCommitMessageProvider {
    public String getMessage(Path currentDir, Path projectDir, String version) {
        if (currentDir.equals(projectDir)) {
            return "chore(release): prepare for version " + version;
        }

        String projectName = currentDir.getFileName().toString();
        return "chore(release): prepare for version " + version + " of " + projectName;
    }
}
