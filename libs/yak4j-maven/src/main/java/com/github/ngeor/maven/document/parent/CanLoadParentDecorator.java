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
    public Optional<CanLoadParent> loadParent() {
        return parentLoader.loadParent(this).map(d -> decorate(d, parentLoader));
    }

    public static CanLoadParent decorate(DocumentLoader documentLoader, ParentLoader parentLoader) {
        return documentLoader instanceof CanLoadParent c ? c : new CanLoadParentDecorator(documentLoader, parentLoader);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CanLoadParentDecorator other
                && Objects.equals(getDecorated(), other.getDecorated())
                && Objects.equals(parentLoader, other.parentLoader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDecorated(), parentLoader);
    }
}
