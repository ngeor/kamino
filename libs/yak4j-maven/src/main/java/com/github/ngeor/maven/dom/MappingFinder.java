package com.github.ngeor.maven.dom;

import java.util.function.Function;

public class MappingFinder<E, I, O> implements Finder<E, O> {
    private final Finder<E, I> decorated;
    private final Function<I, O> mapper;

    public MappingFinder(Finder<E, I> decorated, Function<I, O> mapper) {
        this.decorated = decorated;
        this.mapper = mapper;
    }

    @Override
    public boolean keepSearching() {
        return decorated.keepSearching();
    }

    @Override
    public boolean isEmpty() {
        return decorated.isEmpty();
    }

    @Override
    public boolean accept(E element) {
        return decorated.accept(element);
    }

    @Override
    public O toResult() {
        return mapper.apply(decorated.toResult());
    }
}
