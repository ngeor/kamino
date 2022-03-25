package buzzstats.api;

import org.immutables.value.Value;

import java.time.LocalDateTime;

/** Thing model. */
@Value.Immutable
public interface Thing {
    long getId();

    String getTitle();

    String getUrl();

    int getScore();

    String getUsername();

    LocalDateTime getPublishedAt();

    int getComments();

    String getInternalUrl();

    LocalDateTime getCreatedAt();

    LocalDateTime getLastModifiedAt();

    LocalDateTime getLastCheckedAt();
}
