package com.github.ngeor.web2.db;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link PipelineStats}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class PipelineStatsIT {
    @Autowired
    private PipelineStats pipelineStats;

    @Test
    void deploymentsPerRepository() {
        List<Pair<Repository, Long>> deployments = pipelineStats.deploymentsPerRepository();
        assertThat(deployments).isEmpty();
    }

    @Test
    void buildsPerUser() {
        List<Pair<String, Long>> result = pipelineStats.buildsPerUser();
        assertThat(result).isEmpty();
    }
}
