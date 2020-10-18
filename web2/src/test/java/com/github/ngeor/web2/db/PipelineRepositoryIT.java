package com.github.ngeor.web2.db;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link PipelineRepository}.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class PipelineRepositoryIT {
  @Autowired private PipelineRepository pipelineRepository;

  @Autowired private RepositoryRepository repositoryRepository;

  @Autowired private UserRepository userRepository;

  private Repository repository;
  private Repository repository2;
  private Pipeline pipeline;

  @BeforeEach
  void beforeEach() {
    // arrange
    repository = new Repository();
    repository.setOwner("acme");
    repository.setSlug("repo");
    repository.setUuid("1234");
    repositoryRepository.saveAndFlush(repository);

    repository2 = new Repository();
    repository2.setOwner("acme");
    repository2.setSlug("repo2");
    repository2.setUuid("2345");
    repositoryRepository.saveAndFlush(repository2);

    User creator = new User();
    creator.setUuid("abcd");
    creator.setDisplayName("John Doe");
    userRepository.saveAndFlush(creator);

    pipeline = new Pipeline();
    pipeline.setUuid("1234-1234");
    pipeline.setRepository(repository);
    pipeline.setCreator(creator);
    pipeline.setState("COMPLETED");
    pipeline.setResult("SUCCESSFUL");
    pipeline.setCreatedOn(OffsetDateTime.now(Clock.systemUTC()));
    pipeline.setTriggerName("N/A");
    pipelineRepository.saveAndFlush(pipeline);
  }

  @Test
  void findAllByRepositoryAndStateAndResult_success() {
    // act
    var result = pipelineRepository.findAllByRepositoryAndStateAndResult(
        repository, "COMPLETED", "SUCCESSFUL", Pageable.unpaged());

    // assert
    assertThat(result)
        .extracting(Pipeline::getUuid)
        .containsExactly(pipeline.getUuid());
  }

  @Test
  void findAllByRepositoryAndStateAndResult_wrongRepository() {
    // act
    var result = pipelineRepository.findAllByRepositoryAndStateAndResult(
        repository2, "COMPLETED", "SUCCESSFUL", Pageable.unpaged());

    // assert
    assertThat(result).isEmpty();
  }

  @Test
  void findAllByRepositoryAndStateAndResult_wrongState() {
    // act
    var result = pipelineRepository.findAllByRepositoryAndStateAndResult(
        repository, "RUNNING", "SUCCESSFUL", Pageable.unpaged());

    // assert
    assertThat(result).isEmpty();
  }

  @Test
  void findAllByRepositoryAndStateAndResult_wrongResult() {
    // act
    var result = pipelineRepository.findAllByRepositoryAndStateAndResult(
        repository, "COMPLETED", "FAILED", Pageable.unpaged());

    // assert
    assertThat(result).isEmpty();
  }
}
