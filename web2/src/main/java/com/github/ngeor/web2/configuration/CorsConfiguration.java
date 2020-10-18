package com.github.ngeor.web2.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration.
 */
@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {
  /**
   * The allowed origin.
   */
  public static final String ORIGIN = "http://localhost:4200";

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins(ORIGIN);
  }
}
