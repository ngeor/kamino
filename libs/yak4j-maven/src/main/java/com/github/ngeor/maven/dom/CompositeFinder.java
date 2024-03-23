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
    public boolean stopSearching() {
        return left.stopSearching() && right.stopSearching();
    }

    @Override
    public void accept(I element) {
        left.accept(element);
        right.accept(element);
    }

    @Override
    public Pair<L, R> toResult() {
        return Pair.of(left.toResult(), right.toResult());
    }
}
