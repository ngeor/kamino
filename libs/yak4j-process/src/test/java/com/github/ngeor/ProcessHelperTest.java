package com.github.ngeor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
class ProcessHelperTest {
    /**
     * Rigorous Test.
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testApp() throws IOException, InterruptedException {
        ProcessHelper processHelper = new ProcessHelper(
            new File("."),
            "ls"
        );
        String result = processHelper.run();
        assertTrue(result != null && !result.isBlank());
    }
}
