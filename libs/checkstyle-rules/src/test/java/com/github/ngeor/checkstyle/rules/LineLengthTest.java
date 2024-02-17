package com.github.ngeor.checkstyle.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the LineLength check.
 */
@SuppressWarnings("checkstyle:MagicNumber")
class LineLengthTest {
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
        final String file = "LineLengthTooLong.java";
        final int errorCount = Utils.process(checker, file);
        assertThat(errorCount).isEqualTo(1);
        assertThat(eventCollector.getAuditEvents()).hasSize(1);
        AuditEvent auditEvent = eventCollector.getAuditEvents().get(0);
        assertThat(auditEvent.getLine()).isEqualTo(9);
        assertThat(auditEvent.getColumn()).isEqualTo(0);
        assertThat(auditEvent.getSeverityLevel()).isEqualTo(SeverityLevel.ERROR);
        assertThat(auditEvent.getSourceName())
                .isEqualTo("com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck");
        assertThat(auditEvent.getFileName()).isEqualTo(Utils.expectedFileName(file));
    }
}
