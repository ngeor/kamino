package com.github.ngeor.maven.document;

import java.util.Optional;

@FunctionalInterface
public interface Repository {
    Optional<PomDocument> findParent(PomDocument pomDocument);
}
