package com.github.ngeor;

import com.github.ngeor.maven.Maven;
import com.github.ngeor.maven.MavenDocument;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        Maven maven = new Maven(rootDirectory.toPath().resolve("pom.xml").toFile());
        MavenDocument document = maven.effectivePomNgResolveParent(new ArrayList<>());
        return document.modules().filter(this::isEligible);
    }

    private boolean isEligible(String module) {
        return module != null
                && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }
}
