package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * Unit test for simple App.
 */
class AppTest {
    private final App app = new App();
    private final CommandLine commandLine = new CommandLine(app).setCaseInsensitiveEnumValuesAllowed(true);

    @Test
    void testHelp() {
        commandLine.usage(System.out);
    }

    @Test
    void testApp() {
        assertThrows(CommandLine.MissingParameterException.class, commandLine::parseArgs);
    }

    @Test
    void testParseSuccess() {
        commandLine.parseArgs("-t", "pip", "0.1.2");
        assertEquals(ProjectType.PIP, app.getType());
        assertEquals("0.1.2", app.getVersion());
        assertTrue(app.isFailOnPendingChanges());
        assertTrue(app.isPush());
    }

    @Test
    void testFailOnPendingChanges() {
        commandLine.parseArgs("--fail-on-pending-changes", "-t", "npm", "0.1.2");
        assertTrue(app.isFailOnPendingChanges());
    }

    @Test
    void testNoFailOnPendingChanges() {
        commandLine.parseArgs("--no-fail-on-pending-changes", "-t", "npm", "0.1.2");
        assertFalse(app.isFailOnPendingChanges());
    }

    @Test
    void testPush() {
        commandLine.parseArgs("--push", "-t", "npm", "0.1.2");
        assertTrue(app.isPush());
    }

    @Test
    void testNoPush() {
        commandLine.parseArgs("--no-push", "-t", "npm", "0.1.2");
        assertFalse(app.isPush());
    }
}
