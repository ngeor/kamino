package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.maven.document.property.CanResolveProperties;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class CacheCanResolveProperties implements CanResolveProperties {
    private final CanResolveProperties decorated;
    private DocumentWrapper document;
    private CanLoadParent parent;
    private boolean initializedParent;
    private DocumentWrapper effectivePom;
    private DocumentWrapper properties;

    public CacheCanResolveProperties(CanResolveProperties decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public File getPomFile() {
        return decorated.getPomFile();
    }

    @Override
    public DocumentWrapper loadDocument() {
        if (document == null) {
            document = decorated.loadDocument();
        }
        return document;
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        if (!initializedParent) {
            initializedParent = true;
            parent = decorated.loadParent().orElse(null);
        }
        return Optional.ofNullable(parent);
    }

    @Override
    public DocumentWrapper effectivePom() {
        if (effectivePom == null) {
            effectivePom = decorated.effectivePom();
        }
        return effectivePom;
    }

    @Override
    public DocumentWrapper resolveProperties() {
        if (properties == null) {
            properties = CanResolveProperties.super.resolveProperties();
        }
        return properties;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CacheCanResolveProperties other && Objects.equals(decorated, other.decorated);
    }

    @Override
    public int hashCode() {
        return decorated.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", CacheCanResolveProperties.class.getSimpleName(), decorated);
    }
}
