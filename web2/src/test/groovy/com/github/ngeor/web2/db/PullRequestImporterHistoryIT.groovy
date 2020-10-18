package com.github.ngeor.web2.db

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.time.Clock
import java.time.LocalDateTime

trait RepositoryDSL {
  @Autowired
  RepositoryRepository repositoryRepository

  Repository createRepository(String slug = 'slug') {
    return repositoryRepository.saveAndFlush(new Repository(
      owner: 'acme',
      slug: slug,
      uuid: slug
    ))
  }
}

trait PullRequestImporterHistoryDSL {
  @Autowired
  PullRequestImporterHistoryRepository pullRequestImporterHistoryRepository

  PullRequestImporterHistory createPullRequestImporterHistory(Repository repository) {
    return pullRequestImporterHistoryRepository.saveAndFlush(new PullRequestImporterHistory(
      repository: repository,
      lastCheckedAt: LocalDateTime.now(Clock.systemUTC())
    ))
  }
}

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PullRequestImporterHistoryIT implements RepositoryDSL, PullRequestImporterHistoryDSL {
  @Test
  void existsByRepository_false() {
    // arrange
    Repository repository = createRepository('repo1')

    // act
    boolean result = pullRequestImporterHistoryRepository.existsByRepository(repository)

    // assert
    assert !result
  }

  @Test
  void existsByRepository_true() {
    // arrange
    Repository repository = createRepository('repo1')
    createPullRequestImporterHistory(repository)

    // act
    boolean result = pullRequestImporterHistoryRepository.existsByRepository(repository)

    // assert
    assert result
  }
}
