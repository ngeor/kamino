package com.github.ngeor.maven.dom;

import java.util.Iterator;

public class CountingIterator<E> implements Iterator<E> {
    private final Iterator<E> delegate;
    private int hasNextCount;
    private int nextCount;

    public CountingIterator(Iterator<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        hasNextCount++;
        return delegate.hasNext();
    }

    @Override
    public E next() {
        nextCount++;
        return delegate.next();
    }

    public int getHasNextCount() {
        return hasNextCount;
    }

    public int getNextCount() {
        return nextCount;
    }
}
