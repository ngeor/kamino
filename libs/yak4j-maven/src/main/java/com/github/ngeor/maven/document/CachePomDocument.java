package com.github.ngeor.maven.document;

import java.io.File;

class CachePomDocument extends PomDocument {
    private final Lazy<EffectiveDocument> lazyEffectiveDocument;
    private final Lazy<Graph<String>> lazyDependencyGraph;
    private final Lazy<Graph<String>> lazyAncestorsGraph;

    protected CachePomDocument(PomDocumentFactory owner, File pomFile) {
        super(owner, pomFile);
        this.lazyEffectiveDocument = new Lazy<>(super::toEffective);
        this.lazyDependencyGraph = new Lazy<>(super::createDependencyGraph);
        this.lazyAncestorsGraph = new Lazy<>(super::createAncestorsGraph);
    }

    @Override
    public EffectiveDocument toEffective() {
        return lazyEffectiveDocument.get();
    }

    @Override
    protected Graph<String> createDependencyGraph() {
        return lazyDependencyGraph.get();
    }

    @Override
    protected Graph<String> createAncestorsGraph() {
        return lazyAncestorsGraph.get();
    }
}
