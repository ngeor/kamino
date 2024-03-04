package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactoryDecorator;
import java.util.Objects;
import java.util.Optional;

public final class CanLoadParentFactory extends DocumentLoaderFactoryDecorator<DocumentLoader, CanLoadParent> {
    private final ParentPomFinder parentPomFinder;

    public CanLoadParentFactory(DocumentLoaderFactory<DocumentLoader> decorated, ParentPomFinder parentPomFinder) {
        super(decorated);
        this.parentPomFinder = Objects.requireNonNull(parentPomFinder);
    }

    @Override
    protected CanLoadParent decorateDocumentLoader(DocumentLoader inner) {
        return new CanLoadParentAdaptor(inner, this);
    }

    Optional<CanLoadParent> loadParent(DocumentLoader loader) {
        return parentPomFinder.findParentPom(loader).map(this::createDocumentLoader);
    }
}
