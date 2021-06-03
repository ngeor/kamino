package com.github.ngeor.web2.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Pull request approver.
 */
@Entity
public class PullRequestApprover implements Serializable {
    @EmbeddedId
    private PullRequestApproverId id;

    public PullRequestApproverId getId() {
        return id;
    }

    public void setId(PullRequestApproverId id) {
        this.id = id;
    }
}
