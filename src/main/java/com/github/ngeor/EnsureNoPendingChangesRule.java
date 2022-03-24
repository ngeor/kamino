package com.github.ngeor;

import java.io.IOException;

public class EnsureNoPendingChangesRule implements ValidationRule {
    private final Git git;

    public EnsureNoPendingChangesRule(Git git) {
        this.git = git;
    }

    @Override
    public void validate() throws IOException, InterruptedException {
        if (git.hasPendingChanges()) {
            throw new IllegalStateException("Git has pending changes");
        }
    }
}
