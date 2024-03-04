package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;

class CachedDocumentDecorator extends DocumentLoaderDecorator {
    private final CachedDocumentLoaderFactory creator;

    public CachedDocumentDecorator(DocumentLoader decorated, CachedDocumentLoaderFactory creator) {
        super(decorated);
        this.creator = Objects.requireNonNull(creator);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return creator.getDocumentCache().computeIfAbsent(getPomFile(), super::loadDocument);
    }
}
