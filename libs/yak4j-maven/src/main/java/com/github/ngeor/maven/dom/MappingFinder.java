package com.github.ngeor.maven.dom;

import java.util.function.Function;

public class MappingFinder<I, O, U> implements Finder<I, U> {
    private final Finder<I, O> decorated;
    private final Function<O, U> mapper;

    public MappingFinder(Finder<I, O> decorated, Function<O, U> mapper) {
        this.decorated = decorated;
        this.mapper = mapper;
    }

    @Override
    public boolean stopSearching() {
        return decorated.stopSearching();
    }

    @Override
    public void accept(I element) {
        decorated.accept(element);
    }

    @Override
    public U toResult() {
        O result = decorated.toResult();
        return result == null ? null : mapper.apply(result);
    }
}
