package com.github.ngeor.web2.tasks;

import java.time.Clock;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ngeor.web2.db.Credentials;
import com.github.ngeor.web2.db.CredentialsRepository;
import com.github.ngeor.web2.db.PipelineImporterHistory;
import com.github.ngeor.web2.db.PipelineImporterHistoryRepository;
import com.github.ngeor.web2.db.PullRequestImporterHistory;
import com.github.ngeor.web2.db.PullRequestImporterHistoryRepository;
import com.github.ngeor.web2.db.Repository;
import com.github.ngeor.web2.db.RepositoryRepository;
import com.github.ngeor.web2.mapping.EntityMapper;
import com.github.ngeor.web2.services.BitbucketClientFactory;

/**
 * Imports repositories in the db.
 */
@Component
public class RepositoryImporter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(RepositoryImporter.class);

  @Autowired private RepositoryRepository repositoryRepository;

  @Autowired private CredentialsRepository credentialsRepository;

  @Autowired private BitbucketClientFactory bitbucketClientFactory;

  @Autowired private EntityMapper entityMapper;

  @Autowired
  private PipelineImporterHistoryRepository pipelineImporterHistoryRepository;

  @Autowired
  private PullRequestImporterHistoryRepository
      pullRequestImporterHistoryRepository;

  /**
   * Updates all repositories.
   */
  @Scheduled(fixedDelay = 1000 * 60 * 60)
  public void updateAll() {
    LOGGER.info("Importing repositories");
    for (Credentials credentials : credentialsRepository.findAll()) {
      String owner = credentials.getOwner();
      LOGGER.info("Updating repositories of {}", owner);
      bitbucketClientFactory.bitbucketClient(owner)
          .getAllRepositories()
          .map(r -> entityMapper.toEntity(r, owner))
          .forEach(this ::save);
    }

    LOGGER.info("Finished importing repositories");
  }

  private void save(Repository repository) {
    repositoryRepository.save(repository);
    if (!pipelineImporterHistoryRepository.existsByRepository(repository)) {
      var pipelineImporterHistory = new PipelineImporterHistory();
      pipelineImporterHistory.setLastCheckedAt(
          LocalDateTime.now(Clock.systemUTC()));
      pipelineImporterHistory.setRepository(repository);
      pipelineImporterHistoryRepository.save(pipelineImporterHistory);
    }

    if (!pullRequestImporterHistoryRepository.existsByRepository(repository)) {
      var pullRequestImporterHistory = new PullRequestImporterHistory();
      pullRequestImporterHistory.setLastCheckedAt(
          LocalDateTime.now(Clock.systemUTC()));
      pullRequestImporterHistory.setRepository(repository);
      pullRequestImporterHistoryRepository.save(pullRequestImporterHistory);
    }
  }
}
