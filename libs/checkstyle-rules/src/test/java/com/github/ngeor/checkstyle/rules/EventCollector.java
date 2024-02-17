package com.github.ngeor.checkstyle.rules;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects errors.
 */
class EventCollector implements AuditListener {
    private final List<AuditEvent> auditEvents = new ArrayList<>();

    @Override
    public void auditStarted(AuditEvent event) {
        auditEvents.clear();
    }

    @Override
    public void auditFinished(AuditEvent event) {}

    @Override
    public void fileStarted(AuditEvent event) {}

    @Override
    public void fileFinished(AuditEvent event) {}

    @Override
    public void addError(AuditEvent event) {
        auditEvents.add(event);
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {
        auditEvents.add(event);
    }

    public List<AuditEvent> getAuditEvents() {
        return auditEvents;
    }
}
