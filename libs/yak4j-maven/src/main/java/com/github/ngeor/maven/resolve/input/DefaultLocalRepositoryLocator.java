package com.github.ngeor.maven.resolve.input;

import java.io.File;
import java.nio.file.Path;

public final class DefaultLocalRepositoryLocator implements LocalRepositoryLocator {
    @Override
    public Path localRepository() {
        return new File(System.getProperty("user.home")).toPath().resolve(".m2").resolve("repository");
    }
}
