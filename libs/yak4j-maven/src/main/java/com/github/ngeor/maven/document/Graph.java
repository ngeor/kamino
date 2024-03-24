package com.github.ngeor.maven.document;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph<E> {
    private final Map<E, Set<E>> map = new HashMap<>();

    public void put(E from, E to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if (from.equals(to)) {
            throw new IllegalArgumentException(String.format("Equal objects %s and %s cannot form a path", from, to));
        }
        map.computeIfAbsent(from, ignored -> new HashSet<>()).add(to);
    }

    public boolean hasDirectPath(E from, E to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Set<E> directDestinations = map.get(from);
        return directDestinations != null && directDestinations.contains(to);
    }

    public void visit(E from, Consumer<E> visitor) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(visitor);
        visit(from, visitor, new HashSet<>(Collections.singleton(from)));
    }

    private void visit(E from, Consumer<E> visitor, Set<E> seen) {
        Set<E> directDestinations = map.getOrDefault(from, Collections.emptySet());
        for (E directDestination : directDestinations) {
            if (seen.add(directDestination)) {
                visitor.accept(directDestination);
                visit(directDestination, visitor, seen);
            }
        }
    }
}
