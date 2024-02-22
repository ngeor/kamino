package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitCliffTest {
    private final GitCliff gitCliff = new GitCliff();

    @TempDir
    private Path projectDirectory;

    @Test
    void testBuildArgsSameDirectory() {
        DirContext dirContext = new DirContext(Path.of("."), Path.of("."));
        String[] args = replaceWindowsPaths(gitCliff.buildArgs(dirContext, "1.2.3", Path.of("/tmp/cliff.toml")));
        assertArrayEquals(
                new String[] {"git-cliff", "-o", "CHANGELOG.md", "-t", "1.2.3", "-c", "/tmp/cliff.toml"}, args);
    }

    @Test
    void testBuildArgsDifferentDirectory() {
        Path currentDirectory = projectDirectory.resolve("child");
        assertTrue(currentDirectory.toFile().mkdir());
        String[] args = replaceWindowsPaths(gitCliff.buildArgs(
                new DirContext(currentDirectory, projectDirectory), "2.1.0", Path.of("/tmp/cliff.toml")));
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
                    "2.1.0",
                    "-c",
                    "/tmp/cliff.toml"
                },
                args);
    }

    @Test
    void testBuildArgsGrandChild() {
        Path childDirectory = projectDirectory.resolve("child");
        assertTrue(childDirectory.toFile().mkdir());
        Path grandChildDirectory = childDirectory.resolve("grand-child");
        assertTrue(grandChildDirectory.toFile().mkdir());
        String[] args = replaceWindowsPaths(gitCliff.buildArgs(
                new DirContext(grandChildDirectory, projectDirectory), "0.4.1", Path.of("/tmp/cliff2.toml")));
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
                    "0.4.1",
                    "-c",
                    "/tmp/cliff2.toml"
                },
                args);
    }

    private static String[] replaceWindowsPaths(String[] args) {
        return Stream.of(args).map(s -> s.replace('\\', '/')).toArray(String[]::new);
    }
}
