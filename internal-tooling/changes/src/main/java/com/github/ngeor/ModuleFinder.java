package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        Maven maven = new Maven(rootDirectory.toPath().resolve("pom.xml").toFile());
        DocumentWrapper document = maven.effectivePomNgResolveParent(new ArrayList<>());
        return document.getDocumentElement()
            .findChildElements("modules")
            .flatMap(e -> e.findChildElements("module"))
            .flatMap(e -> e.getTextContentTrimmed().stream())
            .filter(this::isEligible);
    }

    private boolean isEligible(String module) {
        return module != null
            && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }
}
