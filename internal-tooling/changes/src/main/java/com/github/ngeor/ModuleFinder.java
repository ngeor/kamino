package com.github.ngeor;

import com.github.ngeor.maven.ChildMavenModule;
import com.github.ngeor.maven.MavenModuleNg;
import java.io.File;
import java.util.stream.Stream;
import org.apache.commons.lang3.concurrent.ConcurrentException;

public class ModuleFinder {
    public Stream<String> eligibleModules(File rootDirectory) throws ConcurrentException {
        return MavenModuleNg.root(rootDirectory.toPath().resolve("pom.xml").toFile())
                .children()
                .map(ChildMavenModule::getModuleName)
                .filter(this::isEligible);
    }

    private boolean isEligible(String module) {
        return module != null
                && (module.startsWith("archetypes/") || module.startsWith("libs/") || module.startsWith("plugins/"));
    }
}
