package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import java.util.Optional;

@FunctionalInterface
public interface ParentLoader {
    Optional<DocumentLoader> loadParent(DocumentLoader input);
}
