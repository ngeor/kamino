package com.github.ngeor.maven.document.loader.cache;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Map;
import java.util.Objects;

public class CachedDocumentDecorator extends DocumentLoaderDecorator {
    private final DocumentCache cache;

    public CachedDocumentDecorator(DocumentLoader decorated, DocumentCache cache) {
        super(decorated);
        this.cache = Objects.requireNonNull(cache);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return cache.computeIfAbsent(new CanonicalFile(getPomFile()), ignored -> super.loadDocument());
    }

    public static DocumentLoader decorateDocumentLoader(DocumentLoader input, DocumentCache cache) {
        return input instanceof CachedDocumentDecorator ? input : new CachedDocumentDecorator(input, cache);
    }

    public static DocumentLoaderFactory decorateFactory(DocumentLoaderFactory factory, DocumentCache cache) {
        return pomFile -> CachedDocumentDecorator.decorateDocumentLoader(factory.createDocumentLoader(pomFile), cache);
    }

    public static DocumentLoaderFactory decorateFactory(
            DocumentLoaderFactory factory, Map<CanonicalFile, DocumentWrapper> cache) {
        return decorateFactory(factory, cache::computeIfAbsent);
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
