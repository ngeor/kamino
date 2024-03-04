package com.github.ngeor.maven.document.loader;

import java.io.File;
import java.util.Objects;

/**
 * Base class for decorators of {@link DocumentLoaderFactory}.
 * @param <I> The type of {@link DocumentLoader} instances created by the decorated factory.
 * @param <O> The type of {@link DocumentLoader} instances created by this factory.
 */
public abstract class DocumentLoaderFactoryDecorator<I extends DocumentLoader, O extends DocumentLoader>
        implements DocumentLoaderFactory<O> {
    private final DocumentLoaderFactory<I> decorated;

    protected DocumentLoaderFactoryDecorator(DocumentLoaderFactory<I> decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public O createDocumentLoader(File pomFile) {
        return decorateDocumentLoader(decorated.createDocumentLoader(pomFile));
    }

    protected abstract O decorateDocumentLoader(I inner);
}
