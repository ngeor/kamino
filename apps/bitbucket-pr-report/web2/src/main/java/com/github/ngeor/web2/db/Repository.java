package com.github.ngeor.web2.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Repository entity.
 */
@Entity
public class Repository {
  @Id private String uuid;

  private String slug;

  private String owner;

  @OneToOne(mappedBy = "repository")
  private PipelineImporterHistory pipelineImporterHistory;

  @OneToOne(mappedBy = "repository")
  private PullRequestImporterHistory pullRequestImporterHistory;

  public String getUuid() { return uuid; }

  public void setUuid(String uuid) { this.uuid = uuid; }

  public String getSlug() { return slug; }

  public void setSlug(String slug) { this.slug = slug; }

  public String getOwner() { return owner; }

  public void setOwner(String owner) { this.owner = owner; }

  public PipelineImporterHistory getPipelineImporterHistory() {
    return pipelineImporterHistory;
  }

  public void
  setPipelineImporterHistory(PipelineImporterHistory pipelineImporterHistory) {
    this.pipelineImporterHistory = pipelineImporterHistory;
  }

  public PullRequestImporterHistory getPullRequestImporterHistory() {
    return pullRequestImporterHistory;
  }

  public void setPullRequestImporterHistory(
      PullRequestImporterHistory pullRequestImporterHistory) {
    this.pullRequestImporterHistory = pullRequestImporterHistory;
  }
}
