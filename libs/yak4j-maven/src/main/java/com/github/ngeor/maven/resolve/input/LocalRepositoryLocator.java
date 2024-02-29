package com.github.ngeor.maven.resolve.input;

import java.nio.file.Path;

@FunctionalInterface
public interface LocalRepositoryLocator {
    Path localRepository();
}
