package com.github.ngeor.changelog.group;

public sealed interface CommitInfo permits UntypedCommit, ConventionalCommit {
    String type();

    String scope();

    String description();

    boolean isBreaking();

}
