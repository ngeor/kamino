package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class EnsureNoPendingChangesRuleTest {
    private final Git git = mock(Git.class);
    private final EnsureNoPendingChangesRule rule = new EnsureNoPendingChangesRule(git);

    @Test
    void testValid() throws IOException, InterruptedException {
        when(git.hasPendingChanges()).thenReturn(false);
        assertDoesNotThrow(rule::validate);
    }

    @Test
    void testInvalid() throws IOException, InterruptedException {
        when(git.hasPendingChanges()).thenReturn(true);
        assertThrows(IllegalStateException.class, rule::validate);
    }
}
