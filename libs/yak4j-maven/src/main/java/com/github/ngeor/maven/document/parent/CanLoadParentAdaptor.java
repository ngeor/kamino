package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import java.util.Objects;
import java.util.Optional;

final class CanLoadParentAdaptor extends DocumentLoaderDecorator<DocumentLoader> implements CanLoadParent {
    private final CanLoadParentFactory factory;

    public CanLoadParentAdaptor(DocumentLoader decorated, CanLoadParentFactory factory) {
        super(decorated);
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return factory.loadParent(this);
    }
}
