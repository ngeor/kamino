package com.github.ngeor.web2.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import static com.github.ngeor.web2.configuration.CorsConfiguration.ORIGIN;

/**
 * User repository.
 */
@CrossOrigin(origins = ORIGIN)
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByDisplayName(String displayName);
}
