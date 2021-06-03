package com.github.ngeor.web2.api;

import com.github.ngeor.web2.db.PipelineStats;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for statistics.
 */
@Controller
public class StatsController {
    @Autowired
    private PipelineStats pipelineStats;

    /**
     * Gets the deployments per repository.
     */
    @GetMapping("/stats/deployments-per-repository")
    public ResponseEntity<List<RepositoryStats>> deploymentsPerRepository() {
        return ResponseEntity.ok(
            pipelineStats.deploymentsPerRepository()
                .stream()
                .map(p -> new RepositoryStats(p.getKey().getSlug(), p.getValue()))
                .collect(Collectors.toList()));
    }

    /**
     * Gets the builds per user.
     */
    @GetMapping("/stats/builds-per-user")
    public ResponseEntity<List<UserStats>> buildsPerUser() {
        return ResponseEntity.ok(
            pipelineStats.buildsPerUser()
                .stream()
                .map(p -> new UserStats(p.getKey(), p.getValue()))
                .collect(Collectors.toList()));
    }

    /**
     * Pull requests per user.
     */
    @GetMapping("/stats/prs-per-user")
    public ResponseEntity<List<UserStats>> prsPerUser() {
        return ResponseEntity.ok(
            pipelineStats.prsPerUser()
                .stream()
                .map(p -> new UserStats(p.getKey(), p.getValue()))
                .collect(Collectors.toList()));
    }
}

