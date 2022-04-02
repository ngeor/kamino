package com.github.ngeor;

public class GitTagMessageProvider {
    private final DirContext dirContext;
    private final String version;

    public GitTagMessageProvider(DirContext dirContext, String version) {
        this.dirContext = dirContext;
        this.version = version;
    }

    public String getMessage() {
        if (dirContext.isTopLevelProject()) {
            return "Releasing version " + version;
        }

        return "Releasing version " + version + " of " + dirContext.getProjectName();
    }

    public String getTag() {
        if (dirContext.isTopLevelProject()) {
            return "v" + version;
        }
        return dirContext.getProjectName() + "/" + version;
    }
}
