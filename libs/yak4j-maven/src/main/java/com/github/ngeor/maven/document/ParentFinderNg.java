package com.github.ngeor.maven.document;

import java.util.Optional;

@FunctionalInterface
public interface ParentFinderNg<E extends BasePomDocument> {
    Optional<E> findParent(E pomDocument);
}
