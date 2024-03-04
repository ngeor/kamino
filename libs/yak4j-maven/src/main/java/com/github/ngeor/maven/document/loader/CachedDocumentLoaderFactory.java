package com.github.ngeor.maven.document.loader;

import com.github.ngeor.maven.document.cache.FileCache;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public final class CachedDocumentLoaderFactory implements DocumentLoaderFactory<DocumentLoader> {
    private final DocumentLoaderFactory<DocumentLoader> decorated;
    private final FileCache<DocumentWrapper> documentCache = new FileCache<>();

    public CachedDocumentLoaderFactory(DocumentLoaderFactory<DocumentLoader> decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentLoader createDocumentLoader(File pomFile) {
        return new CachedDocumentDecorator(decorated.createDocumentLoader(pomFile), this);
    }

    protected FileCache<DocumentWrapper> getDocumentCache() {
        return documentCache;
    }
}
