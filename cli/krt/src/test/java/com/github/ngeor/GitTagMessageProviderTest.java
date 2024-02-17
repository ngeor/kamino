package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class GitTagMessageProviderTest {
    @Test
    void testSameDirectory() {
        DirContext dirContext = new DirContext(Path.of("."), Path.of("."));
        GitTagMessageProvider gitTagMessageProvider =
                new GitTagMessageProvider(dirContext, new GitTagPrefix(dirContext), "1.2.3");
        assertEquals("Releasing version 1.2.3", gitTagMessageProvider.getMessage());
        assertEquals("v1.2.3", gitTagMessageProvider.getTag());
    }

    @Test
    void testChildDirectory() {
        DirContext dirContext = new DirContext(Path.of("/tmp/child"), Path.of("/tmp"));
        GitTagMessageProvider gitTagMessageProvider =
                new GitTagMessageProvider(dirContext, new GitTagPrefix(dirContext), "2.3.1");
        assertEquals("Releasing version 2.3.1 of child", gitTagMessageProvider.getMessage());
        assertEquals("child/2.3.1", gitTagMessageProvider.getTag());
    }
}
