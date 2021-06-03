package com.github.ngeor.web2.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import static com.github.ngeor.web2.configuration.CorsConfiguration.ORIGIN;

/**
 * Repository of (git) repositories.
 */
@RepositoryRestResource(excerptProjection = InlineHistory.class)
@CrossOrigin(origins = ORIGIN)
public interface RepositoryRepository
    extends JpaRepository<Repository, String> {
    Page<Repository> findByOwner(String owner, Pageable pageable);
}

