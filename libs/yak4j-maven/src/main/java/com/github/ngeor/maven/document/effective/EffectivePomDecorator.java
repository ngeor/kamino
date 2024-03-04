package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.maven.document.parent.ParentDocumentLoaderIterator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

final class EffectivePomDecorator implements EffectivePom {
    private final CanLoadParent decorated;
    private final Merger merger;

    public EffectivePomDecorator(CanLoadParent decorated, Merger merger) {
        this.decorated = Objects.requireNonNull(decorated);
        this.merger = Objects.requireNonNull(merger);
    }

    @Override
    public DocumentWrapper effectivePom() {
        ParentDocumentLoaderIterator it = new ParentDocumentLoaderIterator(this);
        return merge(it, this);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return decorated.loadDocument();
    }

    @Override
    public File getPomFile() {
        return decorated.getPomFile();
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return decorated.loadParent();
    }

    private DocumentWrapper merge(Iterator<CanLoadParent> it, DocumentLoader child) {
        if (!it.hasNext()) {
            return child.loadDocument();
        }

        DocumentWrapper left = merge(it, it.next());
        return merger.mergeIntoLeft(left, child);
    }
}
