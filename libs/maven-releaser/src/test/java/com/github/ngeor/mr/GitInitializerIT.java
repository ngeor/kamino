package com.github.ngeor.mr;

import static com.github.ngeor.mr.Util.VALID_CHILD_POM_CONTENTS;
import static com.github.ngeor.mr.Util.VALID_PARENT_POM_CONTENTS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.git.Git;
import com.github.ngeor.process.ProcessFailedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitInitializerIT {
    @TempDir
    private Path remoteRoot;

    @TempDir
    private Path monorepoRoot;

    private Git git;

    @BeforeEach
    void beforeEach() throws ProcessFailedException {
        Util.createRemoteGit(remoteRoot, monorepoRoot);
        git = new Git(monorepoRoot.toFile());
    }

    private void act() throws ProcessFailedException {
        GitInitializer.INSTANCE.apply(monorepoRoot.toFile());
    }

    @Test
    void testDefaultBranch() throws IOException, ProcessFailedException {
        // arrange
        Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_PARENT_POM_CONTENTS);
        git.addAll();
        git.commit("chore: Added pom.xml");

        // act
        git.checkoutNewBranch("develop");

        // assert
        assertThatThrownBy(this::act).hasMessage("repo was not on default branch (expected trunk, found develop)");
    }

    @Test
    void testNoUntrackedFiles() throws IOException, ProcessFailedException {
        // arrange
        addCommitWithInvalidXml();
        Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);
        git.addAll();
        git.commit("Fixed pom issues");

        // act
        Files.writeString(monorepoRoot.resolve("pom2.xml"), VALID_CHILD_POM_CONTENTS);

        // assert
        assertThatThrownBy(this::act).hasMessage("repo has untracked files");
    }

    @Test
    void testNoStagedFiles() throws IOException, ProcessFailedException {
        // arrange
        addCommitWithInvalidXml();
        Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);

        // act
        git.addAll();

        // act and assert
        assertThatThrownBy(this::act).hasMessage("repo has staged files");
    }

    @Test
    void testNoModifiedFiles() throws IOException, ProcessFailedException {
        // arrange
        addCommitWithInvalidXml();

        // act
        Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);

        // act and assert
        assertThatThrownBy(this::act).hasMessage("repo has modified files");
    }

    private void addCommitWithInvalidXml() throws IOException, ProcessFailedException {
        Files.writeString(monorepoRoot.resolve("pom.xml"), "dummy");
        git.addAll();
        git.commit("Adding incorrect commit");
    }
}
