package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public final class CanLoadParentFactory implements DocumentLoaderFactory<CanLoadParent> {
    private final DocumentLoaderFactory<DocumentLoader> decorated;
    private final ParentPomFinder parentPomFinder;

    public CanLoadParentFactory(DocumentLoaderFactory<DocumentLoader> decorated, ParentPomFinder parentPomFinder) {
        this.decorated = Objects.requireNonNull(decorated);
        this.parentPomFinder = Objects.requireNonNull(parentPomFinder);
    }

    @Override
    public CanLoadParent createDocumentLoader(File pomFile) {
        return new CanLoadParentDecorator(decorated.createDocumentLoader(pomFile), this);
    }

    Optional<CanLoadParent> loadParent(DocumentLoader loader) {
        return parentPomFinder.findParentPom(loader).map(this::createDocumentLoader);
    }
}
