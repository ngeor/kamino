package com.github.ngeor;

public class GitTagMessageProvider {
    private final DirContext dirContext;
    private final GitTagPrefix gitTagPrefix;
    private final String version;

    public GitTagMessageProvider(DirContext dirContext, GitTagPrefix gitTagPrefix, String version) {
        this.dirContext = dirContext;
        this.gitTagPrefix = gitTagPrefix;
        this.version = version;
    }

    public String getMessage() {
        if (dirContext.isTopLevelProject()) {
            return "Releasing version " + version;
        }

        return "Releasing version " + version + " of " + dirContext.getProjectName();
    }

    public String getTag() {
        return gitTagPrefix.getPrefix() + version;
    }
}
