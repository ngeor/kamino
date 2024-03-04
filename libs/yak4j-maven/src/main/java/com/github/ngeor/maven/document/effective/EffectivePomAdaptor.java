package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.maven.document.parent.CanLoadParentDecorator;
import com.github.ngeor.maven.document.parent.ParentDocumentLoaderIterator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Iterator;
import java.util.Objects;

final class EffectivePomAdaptor extends CanLoadParentDecorator<CanLoadParent> implements EffectivePom {
    private final Merger merger;

    public EffectivePomAdaptor(CanLoadParent decorated, Merger merger) {
        super(decorated);
        this.merger = Objects.requireNonNull(merger);
    }

    @Override
    public DocumentWrapper effectivePom() {
        ParentDocumentLoaderIterator it = new ParentDocumentLoaderIterator(this);
        return merge(it, this);
    }

    private DocumentWrapper merge(Iterator<CanLoadParent> it, DocumentLoader child) {
        if (!it.hasNext()) {
            return child.loadDocument();
        }

        DocumentWrapper left = merge(it, it.next());
        return merger.mergeIntoLeft(left, child);
    }
}
