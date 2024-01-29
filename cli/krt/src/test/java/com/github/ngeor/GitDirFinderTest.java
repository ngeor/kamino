package com.github.ngeor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitDirFinderTest {
    private final GitDirFinder gitDirFinder = new GitDirFinder();
    private Path tempDirectory;

    @BeforeEach
    void setup() throws IOException {
        tempDirectory = Files.createTempDirectory("prefix");
        tempDirectory.toFile().deleteOnExit();
    }

    @Test
    void testNotInGitDir() {
        assertNull(gitDirFinder.find(tempDirectory));
    }

    @Test
    void testInGitDirRoot() {
        IOUtils.createDirectory(tempDirectory, ".git");
        assertEquals(tempDirectory, gitDirFinder.find(tempDirectory));
    }

    @Test
    void testInGitDirSubFolder() {
        Path childDirectory = IOUtils.createDirectory(tempDirectory, "child");
        IOUtils.createDirectory(tempDirectory, ".git");
        assertEquals(tempDirectory, gitDirFinder.find(childDirectory));
    }
}
