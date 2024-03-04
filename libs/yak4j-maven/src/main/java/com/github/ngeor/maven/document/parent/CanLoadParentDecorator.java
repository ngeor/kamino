package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import java.util.Optional;

public class CanLoadParentDecorator<E extends CanLoadParent> extends DocumentLoaderDecorator<E>
        implements CanLoadParent {

    protected CanLoadParentDecorator(E decorated) {
        super(decorated);
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return getDecorated().loadParent();
    }
}
