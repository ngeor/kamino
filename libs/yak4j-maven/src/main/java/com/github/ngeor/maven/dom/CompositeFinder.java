package com.github.ngeor.maven.dom;

import org.apache.commons.lang3.tuple.Pair;

public class CompositeFinder<I, L, R> implements Finder<I, Pair<L, R>> {
    private final Finder<I, L> left;
    private final Finder<I, R> right;

    public CompositeFinder(Finder<I, L> left, Finder<I, R> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean keepSearching() {
        return left.keepSearching() || right.keepSearching();
    }

    @Override
    public boolean isEmpty() {
        return left.isEmpty() || right.isEmpty();
    }

    @Override
    public boolean accept(I element) {
        return left.acceptIfEmpty(element) || right.acceptIfEmpty(element);
    }

    @Override
    public Pair<L, R> toResult() {
        return Pair.of(left.toResult(), right.toResult());
    }
}
