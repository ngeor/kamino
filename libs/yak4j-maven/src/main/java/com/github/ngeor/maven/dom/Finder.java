package com.github.ngeor.maven.dom;

import java.util.Iterator;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;

public interface Finder<I, O> {
    boolean keepSearching();
    boolean isEmpty();
    boolean accept(I element);

    O toResult();

    default O find(Iterator<I> it) {
        while (keepSearching() && it.hasNext()) {
            I next = it.next();
            accept(next);
        }
        return toResult();
    }

    default boolean acceptIfEmpty(I element) {
        if (isEmpty()) {
            return accept(element);
        } else {
            return false;
        }
    }

    default <U> Finder<I, Pair<O, U>> compose(Finder<I, U> other) {
        return new CompositeFinder<>(this, other);
    }

    default <U> Finder<I, U> map(Function<O, U> mapper) {
        return new MappingFinder<>(this, mapper);
    }

    default Finder<I, O> asOptional() {
        return new OptionalFinder<>(this);
    }

}
