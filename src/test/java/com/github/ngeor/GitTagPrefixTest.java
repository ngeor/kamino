package com.github.ngeor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitTagPrefixTest {
    @Test
    void topLevelProject() {
        DirContext dirContext = mock(DirContext.class);
        when(dirContext.isTopLevelProject()).thenReturn(true);
        GitTagPrefix gitTagPrefix = new GitTagPrefix(dirContext);
        assertEquals("v", gitTagPrefix.getPrefix());
    }

    @Test
    void childProject() {
        DirContext dirContext = mock(DirContext.class);
        when(dirContext.getProjectName()).thenReturn("app");
        GitTagPrefix gitTagPrefix = new GitTagPrefix(dirContext);
        assertEquals("app/", gitTagPrefix.getPrefix());
    }
}
