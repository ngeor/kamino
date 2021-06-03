package com.github.ngeor.web2.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of pipelines.
 */
public interface PipelineRepository extends JpaRepository<Pipeline, String> {
    Page<Pipeline> findAllByRepositoryAndStateAndResult(Repository repository,
                                                        String state,
                                                        String result,
                                                        Pageable pageable);
}
