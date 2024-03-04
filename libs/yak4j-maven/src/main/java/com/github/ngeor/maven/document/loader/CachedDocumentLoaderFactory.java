package com.github.ngeor.maven.document.loader;

import com.github.ngeor.maven.document.cache.FileCache;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

/**
 * A factory of {@link DocumentLoader} instances which cache their DOM document and only load it once.
 * Parsing the XML file into a DOM document can be expensive, so this caching can improve performance.
 */
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

    FileCache<DocumentWrapper> getDocumentCache() {
        return documentCache;
    }
}
