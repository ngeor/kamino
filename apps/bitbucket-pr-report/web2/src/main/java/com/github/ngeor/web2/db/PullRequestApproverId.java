package com.github.ngeor.web2.db;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 * Id for {@link PullRequestApprover}.
 */
@Embeddable
public class PullRequestApproverId implements Serializable {
  private PullRequestId pullRequestId;
  private String approverUuid;

  public PullRequestId getPullRequestId() { return pullRequestId; }

  public void setPullRequestId(PullRequestId pullRequestId) {
    this.pullRequestId = pullRequestId;
  }

  public String getApproverUuid() { return approverUuid; }

  public void setApproverUuid(String approverUuid) {
    this.approverUuid = approverUuid;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof PullRequestApproverId) {
      PullRequestApproverId that = (PullRequestApproverId)o;
      return pullRequestId.equals(that.pullRequestId) &&
          approverUuid.equals(that.approverUuid);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pullRequestId, approverUuid);
  }
}
