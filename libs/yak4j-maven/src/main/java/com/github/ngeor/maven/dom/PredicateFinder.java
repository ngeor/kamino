package com.github.ngeor.maven.dom;

import java.util.Objects;
import java.util.function.Predicate;

public class PredicateFinder<T> implements Finder<T, T> {
    private final Predicate<T> predicate;
    private boolean found;
    private T value;

    public PredicateFinder(Predicate<T> predicate) {
        this.predicate = Objects.requireNonNull(predicate);
    }

    @Override
    public boolean keepSearching() {
        return !found;
    }

    @Override
    public boolean isEmpty() {
        return !found;
    }

    @Override
    public boolean accept(T element) {
        if (predicate.test(element)) {
            found = true;
            value = element;
            return true;
        }
        return false;
    }

    @Override
    public T toResult() {
        return value;
    }
}
