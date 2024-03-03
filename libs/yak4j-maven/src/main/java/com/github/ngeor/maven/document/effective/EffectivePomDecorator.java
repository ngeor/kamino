package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.maven.document.parent.ParentDocumentLoaderIterator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Iterator;
import java.util.Optional;

public record EffectivePomDecorator(CanLoadParent canLoadParent) implements EffectivePom {
    @Override
    public DocumentWrapper effectivePom() {
        ParentDocumentLoaderIterator it = new ParentDocumentLoaderIterator(this);
        return merge(it, this);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return canLoadParent.loadDocument();
    }

    @Override
    public File getPomFile() {
        return canLoadParent.getPomFile();
    }

    @Override
    public Optional<CanLoadParent> loadParent() {
        return canLoadParent.loadParent();
    }

    private DocumentWrapper merge(Iterator<CanLoadParent> it, DocumentLoader child) {
        if (!it.hasNext()) {
            return child.loadDocument();
        }

        // TODO: check if this approach merges the same intermediate results (performance)
        DocumentWrapper left = merge(it, it.next()).deepClone();
        return PomMerger.mergeIntoLeft(left, child.loadDocument());
    }

    public static DocumentLoaderFactory<EffectivePom> decorateFactory(DocumentLoaderFactory<CanLoadParent> factory) {
        return pomFile -> new EffectivePomDecorator(factory.createDocumentLoader(pomFile));
    }
}
