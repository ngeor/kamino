package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitDirFinderTest {
    private final GitDirFinder gitDirFinder = new GitDirFinder();

    @TempDir
    private Path tempDirectory;

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
