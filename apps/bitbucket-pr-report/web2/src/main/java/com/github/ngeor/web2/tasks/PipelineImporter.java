package com.github.ngeor.web2.tasks;

import com.github.ngeor.bitbucket.BitbucketClient;
import com.github.ngeor.web2.db.Pipeline;
import com.github.ngeor.web2.db.PipelineImporterHistory;
import com.github.ngeor.web2.db.PipelineImporterHistoryRepository;
import com.github.ngeor.web2.db.PipelineRepository;
import com.github.ngeor.web2.db.Repository;
import com.github.ngeor.web2.mapping.EntityMapper;
import com.github.ngeor.web2.services.BitbucketClientFactory;
import com.github.ngeor.web2.services.UserService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically updates the database from Bitbucket REST API.
 */
@Component
public class PipelineImporter {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(PipelineImporter.class);

    @Autowired
    private BitbucketClientFactory bitbucketClientFactory;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PipelineImporterHistoryRepository pipelineImporterHistoryRepository;

    /**
     * Updates all data.
     */
    @Scheduled(fixedDelay = 5000)
    public void updateAll() {
        LOGGER.info("Importing pipelines");
        Page<PipelineImporterHistory> result = pipelineImporterHistoryRepository.findAll(
            PageRequest.of(0, 1, Sort.Direction.ASC, "lastCheckedAt"));

        result.forEach(this::saveAll);

        LOGGER.info("Finished importing pipelines");
    }

    private void saveAll(PipelineImporterHistory pipelineImporterHistory) {
        saveAll(pipelineImporterHistory.getRepository());
        pipelineImporterHistory.setLastCheckedAt(
            LocalDateTime.now(Clock.systemUTC()));
        pipelineImporterHistoryRepository.save(pipelineImporterHistory);
    }

    private void saveAll(Repository repository) {
        LOGGER.info("Importing pipelines for {}/{}", repository.getOwner(),
            repository.getSlug());

        Page<Pipeline> allByRepositoryAndStateAndResult = pipelineRepository.findAllByRepositoryAndStateAndResult(
            repository, "COMPLETED", "SUCCESSFUL",
            PageRequest.of(0, 1, Sort.Direction.DESC, "createdOn"));
        OffsetDateTime mostRecentSuccessfulPipeline = allByRepositoryAndStateAndResult.stream()
            .map(Pipeline::getCreatedOn)
            .findFirst()
            .orElse(null);

        LOGGER.info("Considering pipelines created after {}",
            mostRecentSuccessfulPipeline);

        BitbucketClient bitbucketClient = bitbucketClientFactory.bitbucketClient(repository.getOwner());
        bitbucketClient.getAllPipelines(repository.getSlug())
            .takeWhile(
                p
                    -> mostRecentSuccessfulPipeline == null || p.getCreatedOn().isAfter(mostRecentSuccessfulPipeline))
            .map(p
                -> entityMapper.toEntity(p, repository,
                userService.ensure(p.getCreator())))
            .forEach(pipelineRepository::save);
        LOGGER.info("Finished importing pipelines for {}/{}", repository.getOwner(),
            repository.getSlug());
    }
}
