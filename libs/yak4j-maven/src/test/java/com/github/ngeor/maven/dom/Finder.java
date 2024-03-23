package com.github.ngeor.maven.dom;

import java.util.Iterator;

public interface Finder<I, O> {
    boolean isEmpty();
    boolean accept(I element);

    O toResult();

    default O find(Iterator<I> it) {
        while (isEmpty() && it.hasNext()) {
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
}
