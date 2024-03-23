package com.github.ngeor.maven.dom;

import java.util.Objects;

public abstract class FinderDecorator<I, O> implements Finder<I, O> {
    private final Finder<I, O> decorated;

    protected FinderDecorator(Finder<I, O> decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public boolean stopSearching() {
        return decorated.stopSearching();
    }

    @Override
    public boolean accept(I element) {
        return decorated.accept(element);
    }

    @Override
    public O toResult() {
        return decorated.toResult();
    }
}
