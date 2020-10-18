package com.github.ngeor.web2.db;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Pull request importer history entity.
 */
@Entity
public class PullRequestImporterHistory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @OneToOne private Repository repository;

  private LocalDateTime lastCheckedAt;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public Repository getRepository() { return repository; }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }

  public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
    this.lastCheckedAt = lastCheckedAt;
  }
}
