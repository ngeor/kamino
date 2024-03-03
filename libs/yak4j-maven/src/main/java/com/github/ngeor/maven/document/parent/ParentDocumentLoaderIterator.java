package com.github.ngeor.maven.document.parent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ParentDocumentLoaderIterator implements Iterator<CanLoadParent> {
    private CanLoadParent next;
    private boolean valueTaken = true;

    public ParentDocumentLoaderIterator(CanLoadParent next) {
        this.next = Objects.requireNonNull(next);
    }

    @Override
    public boolean hasNext() {
        check();
        return next != null;
    }

    @Override
    public CanLoadParent next() {
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
            next = next.loadParent().orElse(null);
        }
    }
}
