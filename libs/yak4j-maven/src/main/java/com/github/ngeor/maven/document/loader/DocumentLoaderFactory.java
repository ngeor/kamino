package com.github.ngeor.maven.document.loader;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * A factory that can create document loaders of a generic type.
 * @param <E> The type of the {@link DocumentLoader} created by this factory.
 */
@FunctionalInterface
public interface DocumentLoaderFactory<E extends DocumentLoader> {
    E createDocumentLoader(File pomFile);

    default E createDocumentLoader(Path pomPath) {
        return createDocumentLoader(pomPath.toFile());
    }

    default E createDocumentLoader(Path directory, String filename) {
        return createDocumentLoader(directory.resolve(filename).toFile());
    }

    default <O extends DocumentLoader> DocumentLoaderFactory<O> decorate(
            Function<DocumentLoaderFactory<E>, DocumentLoaderFactory<O>> decorator) {
        return decorator.apply(this);
    }
}
