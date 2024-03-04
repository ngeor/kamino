package com.github.ngeor.maven.document.parent;

import java.nio.file.Path;

@FunctionalInterface
public interface LocalRepositoryLocator {
    Path localRepository();
}
