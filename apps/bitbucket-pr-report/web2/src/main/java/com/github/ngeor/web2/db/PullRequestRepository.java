package com.github.ngeor.web2.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of pull requests.
 */
public interface PullRequestRepository
    extends JpaRepository<PullRequest, PullRequestId> {
    Page<PullRequest> findAllByRepository(Repository repository,
                                          Pageable pageable);
}
