package com.github.ngeor.web2.db;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Statistics on pipelines.
 */
@Service
public class PipelineStats {
    @Autowired
    private EntityManager entityManager;

    /**
     * Deployments per repository.
     */
    @SuppressWarnings("unchecked")
    public List<Pair<Repository, Long>> deploymentsPerRepository() {
        List<Object[]> resultList =
            entityManager
                .createQuery("SELECT p.repository, COUNT(p) AS c "
                    + "FROM Pipeline p "
                    + "WHERE p.targetRefName=:branch "
                    + "GROUP BY p.repository "
                    + "ORDER BY c DESC")
                .setParameter("branch", "master")
                .getResultList();
        return resultList.stream()
            .map(x -> Pair.of((Repository) x[0], ((Number) x[1]).longValue()))
            .collect(Collectors.toList());
    }

    /**
     * Builds per user.
     */
    @SuppressWarnings("unchecked")
    public List<Pair<String, Long>> buildsPerUser() {
        List<Object[]> resultList =
            entityManager
                .createQuery("SELECT p.creator.displayName, COUNT(p) AS c "
                    + "FROM Pipeline p "
                    + "GROUP BY p.creator.displayName "
                    + "ORDER BY c DESC")
                .getResultList();
        return resultList.stream()
            .map(x -> Pair.of((String) x[0], ((Number) x[1]).longValue()))
            .collect(Collectors.toList());
    }

    /**
     * PRs per user.
     */
    @SuppressWarnings("unchecked")
    public List<Pair<String, Long>> prsPerUser() {
        List<Object[]> resultList =
            entityManager
                .createQuery("SELECT p.author.displayName, COUNT(p) AS c "
                    + "FROM PullRequest p "
                    + "GROUP BY p.author.displayName "
                    + "ORDER BY c DESC")
                .getResultList();
        return resultList.stream()
            .map(x -> Pair.of((String) x[0], ((Number) x[1]).longValue()))
            .collect(Collectors.toList());
    }
}
