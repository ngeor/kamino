package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import java.io.File;
import java.util.Optional;

@FunctionalInterface
public interface ParentPomFinder {
    Optional<File> findParentPom(DocumentLoader input);
}
