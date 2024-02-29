package com.github.ngeor.maven.resolve.input;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ParentInputIterator implements Iterator<Input> {
    private Input next;
    private final ParentLoader parentLoader;
    private boolean valueTaken = true;

    public ParentInputIterator(Input next, ParentLoader parentLoader) {
        this.next = Objects.requireNonNull(next);
        this.parentLoader = Objects.requireNonNull(parentLoader);
    }

    @Override
    public boolean hasNext() {
        check();
        return next != null;
    }

    @Override
    public Input next() {
        check();
        valueTaken = true;
        if (next == null) {
            throw new NoSuchElementException();
        }
        return next;
    }

    private void check() {
        if (valueTaken) {
            valueTaken = false;
            next = parentLoader.loadParent(next).orElse(null);
        }
    }
}
