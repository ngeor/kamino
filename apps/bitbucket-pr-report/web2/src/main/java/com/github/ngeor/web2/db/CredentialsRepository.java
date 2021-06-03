package com.github.ngeor.web2.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import static com.github.ngeor.web2.configuration.CorsConfiguration.ORIGIN;

/**
 * Credentials repository.
 */
@Repository
@CrossOrigin(origins = ORIGIN)
public interface CredentialsRepository
    extends JpaRepository<Credentials, String> {
}
