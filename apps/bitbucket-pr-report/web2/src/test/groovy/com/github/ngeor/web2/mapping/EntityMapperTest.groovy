package com.github.ngeor.web2.mapping

import com.github.ngeor.bitbucket.models.Pipeline
import com.github.ngeor.bitbucket.models.PullRequest
import com.github.ngeor.web2.db.Repository
import com.github.ngeor.web2.db.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

import java.time.temporal.ChronoUnit

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.within

class EntityMapperTest {
    EntityMapper mapper = Mappers.getMapper(EntityMapper.class)

    @Nested
    class Pipeline_ToEntity {
        def pipelineModel = new Pipeline()
        def repository = new Repository()
        def creator = new User()
        com.github.ngeor.web2.db.Pipeline pipelineEntity

        @BeforeEach
        void beforeEach() {
            pipelineEntity = mapper.toEntity(pipelineModel, repository, creator)
        }

        @Test
        void importedAt_isCloseToUtcNow() {
            assertThat(pipelineEntity.importedAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
        }

        @Test
        void lastCheckedAt_isCloseToUtcNow() {
            assertThat(pipelineEntity.lastCheckedAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
        }
    }

    @Nested
    class PullRequest_ToEntity {
        def pullRequestModel = new PullRequest()
        def repository = new Repository()
        def author = new User()
        com.github.ngeor.web2.db.PullRequest pullRequestEntity

        @BeforeEach
        void beforeEach() {
            pullRequestEntity = mapper.toEntity(pullRequestModel, repository, author)
        }

        @Test
        void importedAt_isCloseToUtcNow() {
            assertThat(pullRequestEntity.importedAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
        }

        @Test
        void lastCheckedAt_isCloseToUtcNow() {
            assertThat(pullRequestEntity.lastCheckedAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
        }
    }
}
