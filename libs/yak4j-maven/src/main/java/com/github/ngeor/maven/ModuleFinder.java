package com.github.ngeor.maven;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> findModules(File rootPomFile) {
        Objects.requireNonNull(rootPomFile);
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(rootPomFile);
        return mavenDocument.modules();
    }
}
