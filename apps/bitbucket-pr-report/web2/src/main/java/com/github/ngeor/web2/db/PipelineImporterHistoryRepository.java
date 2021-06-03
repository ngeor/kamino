package com.github.ngeor.web2.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Pipeline importer history repository.
 */
@org.springframework.stereotype.Repository
public interface PipelineImporterHistoryRepository
    extends JpaRepository<PipelineImporterHistory, String> {
    boolean existsByRepository(Repository repository);
}
