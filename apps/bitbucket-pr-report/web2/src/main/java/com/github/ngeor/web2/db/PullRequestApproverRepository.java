package com.github.ngeor.web2.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of pull request approvers.
 */
public interface PullRequestApproverRepository
    extends JpaRepository<PullRequestApprover, PullRequestApproverId> {}
