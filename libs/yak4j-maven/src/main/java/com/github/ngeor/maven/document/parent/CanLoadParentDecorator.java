package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import java.util.Objects;
import java.util.Optional;

public class CanLoadParentDecorator extends DocumentLoaderDecorator implements CanLoadParent {
    private final ParentLoader parentLoader;
    public CanLoadParentDecorator(DocumentLoader decorated, ParentLoader parentLoader) {
        super(decorated);
        this.parentLoader = Objects.requireNonNull(parentLoader);
    }

    @Override
    public Optional<DocumentLoader> loadParent() {
        return parentLoader.loadParent(this);
    }
}
