package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitTagPrefixTest {
    @TempDir
    private Path rootDirectory;

    @Test
    void topLevelProject() {
        DirContext dirContext = new DirContext(rootDirectory, rootDirectory);
        GitTagPrefix gitTagPrefix = new GitTagPrefix(dirContext);
        assertEquals("v", gitTagPrefix.getPrefix());
    }

    @Test
    void childProject() {
        rootDirectory.resolve("app").toFile().mkdir();
        DirContext dirContext = new DirContext(rootDirectory.resolve("app"), rootDirectory);
        GitTagPrefix gitTagPrefix = new GitTagPrefix(dirContext);
        assertEquals("app/", gitTagPrefix.getPrefix());
    }
}
