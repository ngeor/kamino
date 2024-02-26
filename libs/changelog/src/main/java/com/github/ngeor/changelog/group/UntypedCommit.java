package com.github.ngeor.changelog.group;

import com.github.ngeor.git.Commit;

public record UntypedCommit(Commit commit) implements CommitInfo {
    @Override
    public String type() {
        return null;
    }

    @Override
    public String scope() {
        return null;
    }

    @Override
    public String description() {
        return commit.summary();
    }

    @Override
    public boolean isBreaking() {
        return false;
    }
}
