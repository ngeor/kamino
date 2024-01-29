package com.github.ngeor;

import java.io.IOException;
import java.util.Objects;

public class EnsureOnDefaultBranchRule implements ValidationRule {
    private final Git git;

    public EnsureOnDefaultBranchRule(Git git) {
        this.git = git;
    }

    @Override
    public void validate() throws IOException, InterruptedException {
        String defaultBranch = git.defaultBranch();
        String currentBranch = git.currentBranch();
        if (!Objects.equals(defaultBranch, currentBranch)) {
            throw new IllegalStateException("Not on default branch");
        }
    }
}
