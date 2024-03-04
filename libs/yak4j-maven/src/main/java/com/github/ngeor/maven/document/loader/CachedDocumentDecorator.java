package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CachedDocumentDecorator extends DocumentLoaderDecorator {
    private final Map<CanonicalFile, DocumentWrapper> cache;

    public CachedDocumentDecorator(DocumentLoader decorated, Map<CanonicalFile, DocumentWrapper> cache) {
        super(decorated);
        this.cache = Objects.requireNonNull(cache);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return cache.computeIfAbsent(new CanonicalFile(getPomFile()), ignored -> super.loadDocument());
    }

    public static DocumentLoader decorateDocumentLoader(
            DocumentLoader input, Map<CanonicalFile, DocumentWrapper> cache) {
        return input instanceof CachedDocumentDecorator ? input : new CachedDocumentDecorator(input, cache);
    }

    public static DocumentLoaderFactory<DocumentLoader> decorateFactory(
            DocumentLoaderFactory<DocumentLoader> factory, Map<CanonicalFile, DocumentWrapper> cache) {
        return pomFile -> CachedDocumentDecorator.decorateDocumentLoader(factory.createDocumentLoader(pomFile), cache);
    }

    public static DocumentLoaderFactory<DocumentLoader> decorateFactory(DocumentLoaderFactory<DocumentLoader> factory) {
        return decorateFactory(factory, new HashMap<>());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CachedDocumentDecorator other
                && Objects.equals(cache, other.cache)
                && Objects.equals(getDecorated(), other.getDecorated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cache, getDecorated());
    }
}
