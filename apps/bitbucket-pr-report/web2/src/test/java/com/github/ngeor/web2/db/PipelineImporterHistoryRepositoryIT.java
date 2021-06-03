package com.github.ngeor.web2.db;

import java.time.Clock;
import java.time.LocalDateTime;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link PipelineImporterHistoryRepository}.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
class PipelineImporterHistoryRepositoryIT {
    @Autowired
    private PipelineImporterHistoryRepository pipelineImporterHistoryRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    private Repository repository;

    @BeforeEach
    void beforeEach() {
        repository = new Repository();
        repository.setUuid("1234");
        repository.setSlug("repo");
        repository.setOwner("acme");
        repositoryRepository.saveAndFlush(repository);
    }

    @Test
    void existsByRepository_doesNotExist() {
        // act and assert
        assertThat(pipelineImporterHistoryRepository.existsByRepository(repository))
            .isFalse();
    }

    @Test
    void existsByRepository_exists() {
        // arrange
        PipelineImporterHistory pipelineImporterHistory =
            new PipelineImporterHistory();
        pipelineImporterHistory.setRepository(repository);
        pipelineImporterHistory.setLastCheckedAt(
            LocalDateTime.now(Clock.systemUTC()));
        pipelineImporterHistoryRepository.saveAndFlush(pipelineImporterHistory);

        // act and assert
        assertThat(pipelineImporterHistoryRepository.existsByRepository(repository))
            .isTrue();
    }

    @Test
    void repositoryIsUnique() {
        // arrange
        PipelineImporterHistory pipelineImporterHistory =
            new PipelineImporterHistory();
        pipelineImporterHistory.setRepository(repository);
        pipelineImporterHistory.setLastCheckedAt(
            LocalDateTime.now(Clock.systemUTC()));
        pipelineImporterHistoryRepository.saveAndFlush(pipelineImporterHistory);

        PipelineImporterHistory pipelineImporterHistory2 =
            new PipelineImporterHistory();
        pipelineImporterHistory2.setRepository(repository);
        pipelineImporterHistory2.setLastCheckedAt(
            LocalDateTime.now(Clock.systemUTC()));
        assertThatThrownBy(()
            -> pipelineImporterHistoryRepository.saveAndFlush(
            pipelineImporterHistory2))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasCauseInstanceOf(ConstraintViolationException.class);
    }
}
