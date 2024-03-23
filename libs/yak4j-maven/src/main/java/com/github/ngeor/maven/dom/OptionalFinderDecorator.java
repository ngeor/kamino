package com.github.ngeor.maven.dom;

import java.util.function.Supplier;

public class OptionalFinderDecorator<I, O> extends FinderDecorator<I, O> {
    private final Supplier<Boolean> stopSearching;

    public OptionalFinderDecorator(Finder<I, O> decorated, Supplier<Boolean> stopSearching) {
        super(decorated);
        this.stopSearching = stopSearching;
    }

    @Override
    public boolean stopSearching() {
        return super.stopSearching() || stopSearching.get();
    }
}
