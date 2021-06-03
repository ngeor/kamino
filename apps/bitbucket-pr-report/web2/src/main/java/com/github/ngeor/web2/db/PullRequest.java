package com.github.ngeor.web2.db;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * Pull request entity.
 */
@Entity
public class PullRequest {
    @EmbeddedId
    private PullRequestId id;

    @MapsId("repositoryUuid")
    @ManyToOne
    private Repository repository;

    @ManyToOne
    private User author;

    private OffsetDateTime createdOn;
    private OffsetDateTime updatedOn;
    private String title;
    @Column(updatable = false)
    private LocalDateTime importedAt;
    private LocalDateTime lastCheckedAt;

    public PullRequestId getId() {
        return id;
    }

    public void setId(PullRequestId id) {
        this.id = id;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public OffsetDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(OffsetDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
