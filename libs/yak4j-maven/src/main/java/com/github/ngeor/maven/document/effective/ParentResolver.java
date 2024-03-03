package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.parent.CanLoadParent;

public interface ParentResolver {
    DocumentLoader resolveWithParentRecursively(DocumentLoader input);

    default DocumentLoaderFactory<EffectivePom> decorateFactory(DocumentLoaderFactory<CanLoadParent> factory) {
        return pomFile -> new EffectivePomDecorator(factory.createDocumentLoader(pomFile), this);
    }
}
