package com.github.ngeor;

import com.github.ngeor.maven.MavenDocument;
import java.io.File;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        return MavenDocument.effectivePomWithoutResolvingProperties(
                        rootDirectory.toPath().resolve("pom.xml"))
                .modules()
                .filter(this::isEligible);
    }

    private boolean isEligible(String module) {
        return module != null
                && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }
}
