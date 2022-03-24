package com.github.ngeor;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitCliffTest {
    private final GitCliff gitCliff = new GitCliff();

    @Test
    void testBuildArgsSameDirectory() {
        String[] args = gitCliff.buildArgs(Path.of("."), Path.of("."), "1.2.3");
        assertArrayEquals(
            new String[] {"git-cliff", "-o", "CHANGELOG.md", "-t", "1.2.3"},
            args);
    }

    @Test
    void testBuildArgsDifferentDirectory() throws IOException {
        Path projectDirectory = Files.createTempDirectory("cliff");
        try {
            Path currentDirectory = projectDirectory.resolve("child");
            assertTrue(currentDirectory.toFile().mkdir());
            String[] args = gitCliff.buildArgs(currentDirectory, projectDirectory, "2.1.0");
            assertArrayEquals(
                new String[] {
                    "git-cliff",
                    "--include-path",
                    "child/*",
                    "-r",
                    "..",
                    "-o",
                    "CHANGELOG.md",
                    "-t",
                    "2.1.0"
                },
                args
            );
        } finally {
            IOUtils.deleteDirectory(projectDirectory);
        }
    }

    @Test
    void testBuildArgsGrandChild() throws IOException {
        Path projectDirectory = Files.createTempDirectory("cliff");
        try {
            Path childDirectory = projectDirectory.resolve("child");
            assertTrue(childDirectory.toFile().mkdir());
            Path grandChildDirectory = childDirectory.resolve("grand-child");
            assertTrue(grandChildDirectory.toFile().mkdir());
            String[] args = gitCliff.buildArgs(grandChildDirectory, projectDirectory, "0.4.1");
            assertArrayEquals(
                new String[] {
                    "git-cliff",
                    "--include-path",
                    "child/grand-child/*",
                    "-r",
                    "../..",
                    "-o",
                    "CHANGELOG.md",
                    "-t",
                    "0.4.1"
                },
                args
            );
        } finally {
            IOUtils.deleteDirectory(projectDirectory);
        }
    }
}
