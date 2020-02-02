package buzzstats.api;

import java.time.LocalDateTime;
import org.immutables.value.Value;

/**
 * Thing model.
 */
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
