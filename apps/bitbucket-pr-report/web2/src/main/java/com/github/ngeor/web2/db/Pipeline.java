package com.github.ngeor.web2.db;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Pipeline entity.
 */
@Entity
public class Pipeline {
    @Id
    private String uuid;

    @ManyToOne
    private Repository repository;

    @ManyToOne
    private User creator;
    private String state;
    private String result;
    private OffsetDateTime createdOn;
    private OffsetDateTime completedOn;
    private int durationInSeconds;
    private int buildSecondsUsed;
    private String triggerName;
    private String targetRefName;

    @Column(updatable = false)
    private LocalDateTime importedAt;
    private LocalDateTime lastCheckedAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public OffsetDateTime getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(OffsetDateTime completedOn) {
        this.completedOn = completedOn;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public int getBuildSecondsUsed() {
        return buildSecondsUsed;
    }

    public void setBuildSecondsUsed(int buildSecondsUsed) {
        this.buildSecondsUsed = buildSecondsUsed;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTargetRefName() {
        return targetRefName;
    }

    public void setTargetRefName(String targetRefName) {
        this.targetRefName = targetRefName;
    }

    public LocalDateTime getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }
}
