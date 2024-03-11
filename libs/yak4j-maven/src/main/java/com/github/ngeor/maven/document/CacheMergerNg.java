package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.MavenCoordinates;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CacheMergerNg implements MergerNg {
    private final MergerNg decorated;
    private final Map<MavenCoordinates, EffectivePomDocument> rootCache = new HashMap<>();
    private final Map<MavenCoordinates, Map<MavenCoordinates, EffectivePomDocument>> mergeCache = new HashMap<>();

    public CacheMergerNg(MergerNg decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public EffectivePomDocument merge(PomDocument root) {
        return rootCache.computeIfAbsent(root.coordinates(), c -> decorated.merge(root));
    }

    @Override
    public EffectivePomDocument merge(EffectivePomDocument left, PomDocument right) {
        return mergeCache.computeIfAbsent(left.coordinates(), ignored -> new HashMap<>())
            .computeIfAbsent(right.coordinates(), ignored -> decorated.merge(left, right));
    }
}
