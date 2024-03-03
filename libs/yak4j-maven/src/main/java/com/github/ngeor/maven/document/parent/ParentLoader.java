package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import java.util.Optional;

@FunctionalInterface
public interface ParentLoader {
    // TODO make one "lazy" that doesn't actually load the document,
    // TODO and make one that takes as input the loaded document and its pom file
    Optional<DocumentLoader> loadParent(DocumentLoader input);

    default DocumentLoaderFactory<CanLoadParent> decorateFactory(DocumentLoaderFactory<DocumentLoader> factory) {
        return pomFile -> new CanLoadParentDecorator(factory.createDocumentLoader(pomFile), ParentLoader.this);
    }
}
