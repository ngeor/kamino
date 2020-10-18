package com.github.ngeor.web2.tasks;

import java.time.Clock;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ngeor.bitbucket.models.Participant;
import com.github.ngeor.web2.db.PullRequest;
import com.github.ngeor.web2.db.PullRequestApprover;
import com.github.ngeor.web2.db.PullRequestApproverId;
import com.github.ngeor.web2.db.PullRequestApproverRepository;
import com.github.ngeor.web2.db.PullRequestId;
import com.github.ngeor.web2.db.PullRequestImporterHistory;
import com.github.ngeor.web2.db.PullRequestImporterHistoryRepository;
import com.github.ngeor.web2.db.PullRequestRepository;
import com.github.ngeor.web2.db.Repository;
import com.github.ngeor.web2.db.RepositoryRepository;
import com.github.ngeor.web2.db.User;
import com.github.ngeor.web2.mapping.EntityMapper;
import com.github.ngeor.web2.services.BitbucketClientFactory;
import com.github.ngeor.web2.services.UserService;

/**
 * Periodically updates the database from Bitbucket REST API.
 */
@Component
public class PullRequestImporter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PullRequestImporter.class);

  @Autowired private BitbucketClientFactory bitbucketClientFactory;

  @Autowired
  private PullRequestImporterHistoryRepository
      pullRequestImporterHistoryRepository;

  @Autowired private EntityMapper entityMapper;

  @Autowired private PullRequestRepository pullRequestRepository;

  @Autowired
  private PullRequestApproverRepository pullRequestApproverRepository;

  @Autowired private UserService userService;

  @Autowired private RepositoryRepository repositoryRepository;

  /**
   * Updates all data.
   */
  @Scheduled(fixedDelay = 5000)
  public void updateAll() {
    LOGGER.info("Importing pull requests");
    var result = pullRequestImporterHistoryRepository.findAll(
        PageRequest.of(0, 1, Sort.Direction.ASC, "lastCheckedAt"));

    result.forEach(this ::saveAll);

    LOGGER.info("Finished importing pull requests");
  }

  private void saveAll(PullRequestImporterHistory pullRequestImportHistory) {
    saveAll(pullRequestImportHistory.getRepository());
    pullRequestImportHistory.setLastCheckedAt(
        LocalDateTime.now(Clock.systemUTC()));
    pullRequestImporterHistoryRepository.save(pullRequestImportHistory);
  }

  /**
   * Save all pull requests of a repository.
   */
  private void saveAll(Repository repository) {
    LOGGER.info("Importing pull requests for {}/{}", repository.getOwner(),
                repository.getSlug());

    var allByRepositoryAndStateAndResult =
        pullRequestRepository.findAllByRepository(
            repository, PageRequest.of(0, 1, Sort.Direction.DESC, "createdOn"));
    var mostRecent = allByRepositoryAndStateAndResult.stream()
                         .map(PullRequest::getCreatedOn)
                         .findFirst()
                         .orElse(null);

    LOGGER.info("Considering PRs created after {}", mostRecent);

    var bitbucketClient =
        bitbucketClientFactory.bitbucketClient(repository.getOwner());
    bitbucketClient.getAllMergedPullRequests(repository.getSlug())
        .takeWhile(
            pr -> mostRecent == null || pr.getCreatedOn().isAfter(mostRecent))
        .forEach(pr -> {
          PullRequestId pri = new PullRequestId();
          pri.setId(pr.getId());
          pri.setRepositoryUuid(repository.getUuid());

          PullRequest pullRequest = entityMapper.toEntity(
              pr, repository, userService.ensure(pr.getAuthor()));
          pullRequest.setId(pri);
          pullRequestRepository.save(pullRequest);

          pr.getParticipants()
              .stream()
              .filter(Participant::isApproved)
              .map(Participant::getUser)
              .map(userService::ensure)
              .map(User::getUuid)
              .map(approverUuid -> {
                var result = new PullRequestApprover();
                var rid = new PullRequestApproverId();
                rid.setApproverUuid(approverUuid);
                rid.setPullRequestId(pullRequest.getId());
                result.setId(rid);
                return result;
              })
              .forEach(pullRequestApproverRepository::save);
        });

    LOGGER.info("Finished importing pull requests for {}/{}",
                repository.getOwner(), repository.getSlug());
  }
}
