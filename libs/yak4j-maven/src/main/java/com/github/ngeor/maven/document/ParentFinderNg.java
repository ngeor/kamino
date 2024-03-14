package com.github.ngeor.maven.document;

import java.util.Optional;

@FunctionalInterface
public interface ParentFinderNg {
    Optional<PomDocument> findParent(PomDocument pomDocument);
}
