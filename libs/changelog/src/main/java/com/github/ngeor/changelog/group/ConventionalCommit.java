package com.github.ngeor.changelog.group;

public record ConventionalCommit(String type, String scope, String description, boolean isBreaking)
    implements CommitInfo {
}
