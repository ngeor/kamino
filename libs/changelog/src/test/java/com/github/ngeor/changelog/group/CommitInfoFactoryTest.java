package com.github.ngeor.changelog.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.git.Commit;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CommitInfoFactoryTest {
    @Test
    void testConventionalCommit() {
        // arrange
        Commit commit = new Commit("sha", LocalDate.now(), null, "chore: Fix tests");

        // act
        CommitInfo commitInfo = CommitInfoFactory.toCommitInfo(commit);

        // assert
        assertThat(commitInfo.type()).isEqualTo("chore");
        assertThat(commitInfo.description()).isEqualTo("Fix tests");
        assertThat(commitInfo.scope()).isNull();
        assertThat(commitInfo.isBreaking()).isFalse();
    }

    @Test
    void testConventionalCommitWithScope() {
        // arrange
        Commit commit = new Commit("sha", LocalDate.now(), null, "feat(app): Redesign gui");

        // act
        CommitInfo commitInfo = CommitInfoFactory.toCommitInfo(commit);

        // assert
        assertThat(commitInfo.type()).isEqualTo("feat");
        assertThat(commitInfo.description()).isEqualTo("Redesign gui");
        assertThat(commitInfo.scope()).isEqualTo("app");
        assertThat(commitInfo.isBreaking()).isFalse();
    }

    @Test
    void testConventionalCommitWithBreakingChanges() {
        // arrange
        Commit commit = new Commit("sha", LocalDate.now(), null, "fix!: Changed backend");

        // act
        CommitInfo commitInfo = CommitInfoFactory.toCommitInfo(commit);

        // assert
        assertThat(commitInfo.type()).isEqualTo("fix");
        assertThat(commitInfo.description()).isEqualTo("Changed backend");
        assertThat(commitInfo.scope()).isNull();
        assertThat(commitInfo.isBreaking()).isTrue();
    }

    @Test
    void testConventionalCommitWithScopeAndBreakingChanges() {
        // arrange
        Commit commit = new Commit("sha", LocalDate.now(), null, "refactor(kernel)!: Allow rust");

        // act
        CommitInfo commitInfo = CommitInfoFactory.toCommitInfo(commit);

        // assert
        assertThat(commitInfo.type()).isEqualTo("refactor");
        assertThat(commitInfo.description()).isEqualTo("Allow rust");
        assertThat(commitInfo.scope()).isEqualTo("kernel");
        assertThat(commitInfo.isBreaking()).isTrue();
    }
}
