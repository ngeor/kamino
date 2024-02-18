package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CommitFilterTest {
    private final CommitFilter filter = new CommitFilter();

    @Test
    void testNullCommit() {
        assertThat(filter.test(null)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testNullCommitSubject(String subject) {
        assertThat(test(subject)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "[maven-release-plugin] updated version",
        "chore: Apply spotless",
        "chore: Fix build",
        "chore: Fixing build",
        "chore: Updated changelog",
        "chore: changelog",
        "chore: fixed build",
        "chore: spotless",
        "chore: updated changelog",
        "fix: Fix build",
        "fix: Fix failing tests",
    })
    void testExcludedCommits(String subject) {
        assertThat(test(subject)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "fix: Something important",
        "chore: Something interesting",
        "chore: fixed building"
    })
    void testIncludedCommits(String subject) {
        assertThat(test(subject)).isTrue();
    }

    private boolean test(String subject) {
        return filter.test(new Commit(null, null, null, subject));
    }
}
