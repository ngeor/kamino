package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ParentInputIterator implements Iterator<DocumentLoader> {
    private DocumentLoader next;
    private final ParentLoader parentLoader;
    private boolean valueTaken = true;

    public ParentInputIterator(DocumentLoader next, ParentLoader parentLoader) {
        this.next = Objects.requireNonNull(next);
        this.parentLoader = Objects.requireNonNull(parentLoader);
    }

    @Override
    public boolean hasNext() {
        check();
        return next != null;
    }

    @Override
    public DocumentLoader next() {
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
