package com.github.ngeor.maven.dom;

public class OptionalFinder<I, O> implements Finder<I, O> {
    private final Finder<I, O> decorated;

    public OptionalFinder(Finder<I, O> decorated) {
        this.decorated = decorated;
    }

    @Override
    public boolean keepSearching() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return decorated.isEmpty();
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
