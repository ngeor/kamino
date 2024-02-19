package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GitTagPrefixTest {
    private Path rootDirectory;

    @BeforeEach
    void beforeEach() throws IOException {
        rootDirectory = Files.createTempDirectory("test");
    }

    @AfterEach
    void afterEach() {
        Stream.of(rootDirectory.toFile().listFiles()).forEach(File::delete);
        rootDirectory.toFile().delete();
    }

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
