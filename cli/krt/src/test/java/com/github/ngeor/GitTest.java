package com.github.ngeor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class GitTest {
    @Nested
    class NoRemoteTest {
        private File directory;
        private Git git;

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            directory = Files.createTempDirectory("no-remote").toFile();
            git = new Git(directory);
            git.init();
            git.config("user.name", "John Doe");
            git.config("user.email", "john.doe@noreply.com");
        }

        @AfterEach
        void tearDown() throws IOException {
            IOUtils.deleteDirectory(directory.toPath());
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
        private File remoteDirectory;
        private File bootstrapDirectory;
        private File workingDirectory;
        private Git remoteGit;
        private Git bootstrapGit;
        private Git git;

        @BeforeEach
        void setup() throws IOException, InterruptedException {
            remoteDirectory = Files.createTempDirectory("remote").toFile();
            remoteGit = new Git(remoteDirectory);
            remoteGit.initBare();

            bootstrapDirectory = Files.createTempDirectory("bootstrap").toFile();
            bootstrapGit = new Git(bootstrapDirectory);
            bootstrapGit.clone(remoteDirectory.getAbsolutePath());
            bootstrapGit.config("user.name", "John Doe");
            bootstrapGit.config("user.email", "john.doe@noreply.com");
            Files.writeString(bootstrapDirectory.toPath().resolve("README.md"), "Hello world");
            bootstrapGit.add("README.md");
            bootstrapGit.commit("Initial commit");
            bootstrapGit.push();

            workingDirectory = Files.createTempDirectory("work").toFile();
            git = new Git(workingDirectory);
            git.clone(remoteDirectory.getAbsolutePath());
            git.config("user.name", "John Doe");
            git.config("user.email", "john.doe@noreply.com");
        }

        @AfterEach
        void tearDown() throws IOException {
            IOUtils.deleteDirectory(workingDirectory.toPath());
            IOUtils.deleteDirectory(bootstrapDirectory.toPath());
            IOUtils.deleteDirectory(remoteDirectory.toPath());
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
