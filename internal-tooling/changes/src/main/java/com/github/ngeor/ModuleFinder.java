package com.github.ngeor;

import com.github.ngeor.maven.ng.PomDocumentFactory;
import com.github.ngeor.mr.Defaults;
import java.io.File;
import java.util.stream.Stream;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) {
        return new PomDocumentFactory()
                .create(rootDirectory.toPath().resolve("pom.xml"))
                .modules()
                .filter(Defaults::isEligibleForRelease);
    }
}
