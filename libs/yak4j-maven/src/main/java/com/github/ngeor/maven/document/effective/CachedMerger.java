package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.cache.CanonicalFile;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CachedMerger implements Merger {
    private final Map<CanonicalFile, DocumentWrapper> cache = new HashMap<>();
    private final Merger decorated;

    public CachedMerger(Merger decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper mergeIntoLeft(DocumentWrapper left, DocumentLoader child) {
        return cache.computeIfAbsent(
                new CanonicalFile(child.getPomFile()), ignored -> decorated.mergeIntoLeft(left, child));
    }
}
