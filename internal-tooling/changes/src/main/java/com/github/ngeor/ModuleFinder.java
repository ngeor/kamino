package com.github.ngeor;

import java.io.File;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        return new com.github.ngeor.maven.ModuleFinder().findModules(
                        rootDirectory.toPath().resolve("pom.xml").toFile())
                .filter(this::isEligible);
    }

    private boolean isEligible(String module) {
        return module != null
                && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }
}
