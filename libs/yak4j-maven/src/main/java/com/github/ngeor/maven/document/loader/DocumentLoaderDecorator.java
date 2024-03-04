package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

/**
 * Base class for a decorator of {@link DocumentLoader} that delegates calls to the decorated instance.
 * @param <E> The type of {@link DocumentLoader} this decorator decorates.
 */
public abstract class DocumentLoaderDecorator<E extends DocumentLoader> implements DocumentLoader {
    private final E decorated;

    protected DocumentLoaderDecorator(E decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return decorated.loadDocument();
    }

    @Override
    public File getPomFile() {
        return decorated.getPomFile();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), decorated);
    }

    protected E getDecorated() {
        return decorated;
    }
}
