package com.github.ngeor.web2.mapping;

import java.time.Clock;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.github.ngeor.web2.db.Pipeline;
import com.github.ngeor.web2.db.PullRequest;
import com.github.ngeor.web2.db.Repository;
import com.github.ngeor.web2.db.User;

/**
 * Mapper between entity and models.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = {LocalDateTime.class, Clock.class})
public interface EntityMapper {
  @Mapping(target = "pipelineImporterHistory", ignore = true)
  @Mapping(target = "pullRequestImporterHistory", ignore = true)
  Repository toEntity(com.github.ngeor.bitbucket.models.Repository model,
                      String owner);

  @Mapping(target = "state", source = "model.state.name")
  @Mapping(target = "triggerName", source = "model.trigger.name")
  @Mapping(target = "targetRefName", source = "model.target.refName")
  @Mapping(target = "result", source = "model.state.result.name")
  @Mapping(target = "uuid", source = "model.uuid")
  @Mapping(target = "repository", source = "repository")
  @Mapping(target = "creator", source = "creator")
  @Mapping(target = "importedAt",
           expression = "java(LocalDateTime.now(Clock.systemUTC()))")
  @Mapping(target = "lastCheckedAt",
           expression = "java(LocalDateTime.now(Clock.systemUTC()))")
  Pipeline
  toEntity(com.github.ngeor.bitbucket.models.Pipeline model,
           Repository repository, User creator);

  User toEntity(com.github.ngeor.bitbucket.models.User model);

  @Mapping(target = "id.id", source = "model.id")
  @Mapping(target = "author", source = "author")
  @Mapping(target = "importedAt",
           expression = "java(LocalDateTime.now(Clock.systemUTC()))")
  @Mapping(target = "lastCheckedAt",
           expression = "java(LocalDateTime.now(Clock.systemUTC()))")
  PullRequest
  toEntity(com.github.ngeor.bitbucket.models.PullRequest model,
           Repository repository, User author);
}
