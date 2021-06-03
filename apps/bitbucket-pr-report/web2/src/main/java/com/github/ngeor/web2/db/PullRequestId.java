package com.github.ngeor.web2.db;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 * Id class for {@link PullRequest}.
 */
@Embeddable
public class PullRequestId implements Serializable {
    private int id;
    private String repositoryUuid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepositoryUuid() {
        return repositoryUuid;
    }

    public void setRepositoryUuid(String repositoryUuid) {
        this.repositoryUuid = repositoryUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PullRequestId) {
            PullRequestId that = (PullRequestId) o;
            return id == that.id && repositoryUuid.equals(that.repositoryUuid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, repositoryUuid);
    }
}
