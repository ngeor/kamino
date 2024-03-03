package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class CanLoadParentFactory implements DocumentLoaderFactory<CanLoadParent> {
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CanLoadParentFactory other && Objects.equals(decorated, other.decorated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(decorated);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", CanLoadParentFactory.class.getSimpleName(), decorated);
    }

    Optional<CanLoadParent> loadParent(DocumentLoader loader) {
        return parentPomFinder.findParentPom(loader).map(this::createDocumentLoader);
    }
}
