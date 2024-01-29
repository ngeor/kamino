package com.github.ngeor;

public class GitCommitMessageProvider {
    public String getMessage(DirContext dirContext, String version) {
        if (dirContext.isTopLevelProject()) {
            return "chore(release): prepare for version " + version;
        }

        String projectName = dirContext.getProjectName();
        return "chore(release): prepare for version " + version + " of " + projectName;
    }
}
