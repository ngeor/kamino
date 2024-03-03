package com.github.ngeor.maven.document.parent;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import java.util.Optional;

public interface CanLoadParent extends DocumentLoader {
    Optional<DocumentLoader> loadParent();
}
