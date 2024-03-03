package com.github.ngeor.maven.document.loader;

import java.io.File;
import java.util.function.Function;

@FunctionalInterface
public interface DocumentLoaderFactory<E extends DocumentLoader> {
    E createDocumentLoader(File pomFile);

    default <O extends DocumentLoader> DocumentLoaderFactory<O> decorate(
            Function<DocumentLoaderFactory<E>, DocumentLoaderFactory<O>> decorator) {
        return decorator.apply(this);
    }
}
