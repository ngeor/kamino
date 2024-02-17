package com.github.ngeor.checkstyle.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ImportOrder check.
 */
@SuppressWarnings("checkstyle:MagicNumber")
class ImportOrderTest {
    private EventCollector eventCollector;
    private Checker checker;

    @BeforeEach
    void beforeEach() throws CheckstyleException {
        eventCollector = new EventCollector();
        checker = Utils.createChecker(eventCollector);
    }

    @AfterEach
    void afterEach() {
        checker.destroy();
    }

    @Test
    void success() throws CheckstyleException {
        final String file = "ImportOrder1.java";
        Utils.process(checker, file);
        List<AuditEvent> auditEvents = eventCollector.getAuditEvents();
        auditEvents.removeIf(
                a -> "com.puppycrawl.tools.checkstyle.checks.imports.UnusedImportsCheck".equals(a.getSourceName()));
        assertThat(auditEvents).as("filtered audit events").hasSize(0);
    }
}
