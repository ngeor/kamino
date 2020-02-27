package buzzstats.db;

import buzzstats.api.ImmutableThing;
import buzzstats.api.Thing;
import java.time.LocalDateTime;

/** Thing db model. */
public class ThingEntity {
    private long id;
    private String title;
    private String url;
    private int score;
    private String username;
    private LocalDateTime publishedAt;
    private int comments;
    private String internalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime lastCheckedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getInternalUrl() {
        return internalUrl;
    }

    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    /**
     * Converts to the model object.
     *
     * @return The equivalent model object.
     */
    public Thing toThing() {
        return ImmutableThing.builder()
            .id(getId())
            .title(getTitle())
            .url(getUrl())
            .score(getScore())
            .username(getUsername())
            .publishedAt(getPublishedAt())
            .comments(getComments())
            .internalUrl(getInternalUrl())
            .createdAt(getCreatedAt())
            .lastModifiedAt(getLastModifiedAt())
            .lastCheckedAt(getLastCheckedAt())
            .build();
    }
}
