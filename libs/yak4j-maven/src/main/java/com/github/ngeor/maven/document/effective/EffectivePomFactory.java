package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactoryDecorator;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import java.util.Objects;

public final class EffectivePomFactory extends DocumentLoaderFactoryDecorator<CanLoadParent, EffectivePom> {
    private final Merger merger;

    public EffectivePomFactory(DocumentLoaderFactory<CanLoadParent> decorated, Merger merger) {
        super(decorated);
        this.merger = Objects.requireNonNull(merger);
    }

    @Override
    protected EffectivePom decorateDocumentLoader(CanLoadParent inner) {
        return new EffectivePomAdaptor(inner, merger);
    }
}
