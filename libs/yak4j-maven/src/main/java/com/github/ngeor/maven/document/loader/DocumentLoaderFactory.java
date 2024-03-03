package com.github.ngeor.maven.document.loader;

import java.io.File;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface DocumentLoaderFactory {
    DocumentLoader createDocumentLoader(File pomFile);

    default DocumentLoaderFactory decorate(UnaryOperator<DocumentLoaderFactory> decorator) {
        return decorator.apply(this);
    }
}
