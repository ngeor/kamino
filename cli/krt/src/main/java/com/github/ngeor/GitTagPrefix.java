package com.github.ngeor;

public class GitTagPrefix {
    private final DirContext dirContext;

    public GitTagPrefix(DirContext dirContext) {
        this.dirContext = dirContext;
    }

    public String getPrefix() {
        if (dirContext.isTopLevelProject()) {
            return "v";
        }

        return dirContext.getProjectName() + "/";
    }
}
