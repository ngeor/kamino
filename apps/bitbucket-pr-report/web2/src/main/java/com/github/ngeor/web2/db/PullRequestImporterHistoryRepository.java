package com.github.ngeor.web2.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Pull request importer history repository.
 */
@org.springframework.stereotype.Repository
public interface PullRequestImporterHistoryRepository
    extends JpaRepository<PullRequestImporterHistory, String> {
    boolean existsByRepository(Repository repository);
}
