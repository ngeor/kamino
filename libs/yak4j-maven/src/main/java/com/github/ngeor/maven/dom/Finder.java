package com.github.ngeor.maven.dom;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;

public interface Finder<I, O> {
    boolean stopSearching();

    boolean accept(I element);

    O toResult();

    default boolean hasResult() {
        return toResult() != null;
    }

    default O find(Iterator<I> it) {
        while (!stopSearching() && it.hasNext()) {
            I next = it.next();
            accept(next);
        }
        return toResult();
    }

    default <U> Finder<I, Pair<O, U>> compose(Finder<I, U> other) {
        return new CompositeFinder<>(this, other);
    }

    default <U> Finder<I, Pair<O, U>> compose(Function<Finder<I, O>, Finder<I, U>> provider) {
        return new CompositeFinder<>(this, provider.apply(this));
    }

    default <U> Finder<I, U> map(Function<O, U> mapper) {
        return new MappingFinder<>(this, mapper);
    }

    default Finder<I, O> asOptional(Supplier<Boolean> stopSearching) {
        return new OptionalFinderDecorator<>(this, stopSearching);
    }
}
