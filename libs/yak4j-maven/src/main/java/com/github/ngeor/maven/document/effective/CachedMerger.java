package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.cache.FileCache;
import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;

public final class CachedMerger implements Merger {
    private final FileCache<DocumentWrapper> cache = new FileCache<>();
    private final Merger decorated;

    public CachedMerger(Merger decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper mergeIntoLeft(DocumentWrapper left, DocumentLoader child) {
        return cache.computeIfAbsent(child, x -> decorated.mergeIntoLeft(left, x));
    }
}
