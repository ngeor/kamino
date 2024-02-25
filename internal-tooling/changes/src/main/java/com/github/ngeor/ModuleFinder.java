package com.github.ngeor;

import com.github.ngeor.maven.resolve.MavenDocument;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        return new MavenDocument(DocumentWrapper.parse(
                        rootDirectory.toPath().resolve("pom.xml").toFile()))
                .modules()
                .filter(this::isEligible);
    }

    private boolean isEligible(String module) {
        return module != null
                && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }
}
