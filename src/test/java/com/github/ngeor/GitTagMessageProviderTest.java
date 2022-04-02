package com.github.ngeor;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitTagMessageProviderTest {
    @Test
    void testSameDirectory() {
        GitTagMessageProvider gitTagMessageProvider = new GitTagMessageProvider(
            new DirContext(Path.of("."), Path.of(".")), "1.2.3"
        );
        assertEquals("Releasing version 1.2.3", gitTagMessageProvider.getMessage());
        assertEquals("v1.2.3", gitTagMessageProvider.getTag());
    }

    @Test
    void testChildDirectory() {
        GitTagMessageProvider gitTagMessageProvider = new GitTagMessageProvider(
            new DirContext(Path.of("/tmp/child"), Path.of("/tmp")), "2.3.1"
        );
        assertEquals("Releasing version 2.3.1 of child", gitTagMessageProvider.getMessage());
        assertEquals("child/2.3.1", gitTagMessageProvider.getTag());
    }
}
