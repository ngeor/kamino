package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitTest {
    @Nested
    class NoRemoteTest {
        @TempDir
        private File directory;

        private Git git;

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            git = new GitImpl(directory);
            git.init();
            git.config("user.name", "John Doe");
            git.config("user.email", "john.doe@noreply.com");
        }

        @Test
        void testCurrentBranch() throws IOException, InterruptedException {
            Files.writeString(directory.toPath().resolve("README.md"), "Hello world");
            git.add("README.md");
            git.commit("Initial commit");
            assertEquals("master", git.currentBranch());
        }

        @Test
        void testHasPendingChangesUntrackedFile() throws IOException, InterruptedException {
            Files.writeString(directory.toPath().resolve("README.md"), "Hello world");
            assertTrue(git.hasPendingChanges());
        }

        @Test
        void testHasPendingChangesStagedFile() throws IOException, InterruptedException {
            Files.writeString(directory.toPath().resolve("README.md"), "Hello world");
            git.add("README.md");
            assertTrue(git.hasPendingChanges());
        }

        @Test
        void testHasPendingChangesCommittedFile() throws IOException, InterruptedException {
            Files.writeString(directory.toPath().resolve("README.md"), "Hello world");
            git.add("README.md");
            git.commit("Initial commit");
            assertFalse(git.hasPendingChanges());
        }

        @Test
        void testHasPendingChangesModifiedFile() throws IOException, InterruptedException {
            Files.writeString(directory.toPath().resolve("README.md"), "Hello world");
            git.add("README.md");
            git.commit("Initial commit");
            Files.writeString(directory.toPath().resolve("README.md"), "Hello world again");
            assertTrue(git.hasPendingChanges());
        }
    }

    @Nested
    class NonEmptyRemoteTest {
        @TempDir
        private File remoteDirectory;

        @TempDir
        private File bootstrapDirectory;

        @TempDir
        private File workingDirectory;

        private Git remoteGit;
        private Git bootstrapGit;
        private Git git;

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            remoteGit = new GitImpl(remoteDirectory);
            remoteGit.initBare();

            bootstrapGit = new GitImpl(bootstrapDirectory);
            bootstrapGit.clone(remoteDirectory.getAbsolutePath());
            bootstrapGit.config("user.name", "John Doe");
            bootstrapGit.config("user.email", "john.doe@noreply.com");
            Files.writeString(bootstrapDirectory.toPath().resolve("README.md"), "Hello world");
            bootstrapGit.add("README.md");
            bootstrapGit.commit("Initial commit");
            bootstrapGit.push();

            git = new GitImpl(workingDirectory);
            git.clone(remoteDirectory.getAbsolutePath());
            git.config("user.name", "John Doe");
            git.config("user.email", "john.doe@noreply.com");
        }

        @Test
        void testCurrentBranch() throws IOException, InterruptedException {
            assertEquals("master", git.currentBranch());
        }

        @Test
        void testDefaultBranch() throws IOException, InterruptedException {
            assertEquals("master", git.defaultBranch());
        }

        @Test
        void testCheckoutNewBranch() throws IOException, InterruptedException {
            git.checkoutNewBranch("test");
            assertEquals("test", git.currentBranch());
            assertEquals("master", git.defaultBranch());
        }
    }
}
