package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import java.io.File;
import java.util.Objects;

public final class EffectivePomFactory implements DocumentLoaderFactory<EffectivePom> {
    private final DocumentLoaderFactory<CanLoadParent> decorated;
    private final Merger merger;

    public EffectivePomFactory(DocumentLoaderFactory<CanLoadParent> decorated, Merger merger) {
        this.decorated = Objects.requireNonNull(decorated);
        this.merger = Objects.requireNonNull(merger);
    }

    @Override
    public EffectivePom createDocumentLoader(File pomFile) {
        return new EffectivePomDecorator(decorated.createDocumentLoader(pomFile), merger);
    }
}
