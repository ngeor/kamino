package com.github.ngeor;

import java.util.function.Supplier;

public class GitTagPrefix implements Supplier<String> {
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

    @Override
    public String get() {
        return getPrefix();
    }
}
