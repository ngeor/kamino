package com.github.ngeor;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitCommitMessageProviderTest {
    @Test
    void testSameDirectory() {
        GitCommitMessageProvider gitCommitMessageProvider = new GitCommitMessageProvider();
        String message = gitCommitMessageProvider.getMessage(
            Path.of("."), Path.of("."), "1.2.3"
        );
        assertEquals("chore(release): prepare for version 1.2.3", message);
    }

    @Test
    void testChildDirectory() {
        GitCommitMessageProvider gitCommitMessageProvider = new GitCommitMessageProvider();
        String message = gitCommitMessageProvider.getMessage(
            Path.of("/tmp/child"), Path.of("/tmp"), "3.2.1"
        );
        assertEquals("chore(release): prepare for version 3.2.1 of child", message);
    }
}
