package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ParentPom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public abstract class BaseParentFinderNg<K, V extends PomDocument> implements ParentFinderNg<V> {
    private final Map<K, V> cache = new HashMap<>();
    private final UnaryOperator<V> parentDecorator;

    protected BaseParentFinderNg(UnaryOperator<V> parentDecorator) {
        this.parentDecorator = parentDecorator;
    }

    // TODO two children of same parent should return the same instance
    @Override
    public Optional<V> findParent(V pomDocument) {
        return Optional.ofNullable(cache.computeIfAbsent(cacheKey(pomDocument), ignored -> pomDocument
                .parentPom()
                .map(p -> parentDecorator.apply(doFindParent(pomDocument, p)))
                .orElse(null)));
    }

    protected abstract K cacheKey(V child);

    protected abstract V doFindParent(V child, ParentPom parentPom);
}
