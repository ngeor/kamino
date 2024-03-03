package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import java.util.Objects;
import java.util.Optional;

class CanLoadParentDecorator extends DocumentLoaderDecorator implements CanLoadParent {
    private final CanLoadParentFactory factory;

    public CanLoadParentDecorator(DocumentLoader decorated, CanLoadParentFactory factory) {
        super(decorated);
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return factory.loadParent(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CanLoadParentDecorator other
                && Objects.equals(getDecorated(), other.getDecorated())
                && Objects.equals(factory, other.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDecorated(), factory);
    }
}
