package com.github.ngeor.maven.document.loader;

import com.github.ngeor.maven.document.cache.FileCache;
import com.github.ngeor.yak4jdom.DocumentWrapper;

/**
 * A factory of {@link DocumentLoader} instances which cache their DOM document and only load it once.
 * Parsing the XML file into a DOM document can be expensive, so this caching can improve performance.
 */
public final class CachedDocumentLoaderFactory extends DocumentLoaderFactoryDecorator<DocumentLoader, DocumentLoader> {
    private final FileCache<DocumentWrapper> documentCache = new FileCache<>();

    public CachedDocumentLoaderFactory(DocumentLoaderFactory<DocumentLoader> decorated) {
        super(decorated);
    }

    @Override
    protected DocumentLoader decorateDocumentLoader(DocumentLoader inner) {
        return new CachedDocumentDecorator(inner, this);
    }

    FileCache<DocumentWrapper> getDocumentCache() {
        return documentCache;
    }
}
