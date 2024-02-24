package com.github.ngeor.maven;

import java.io.File;
import java.util.stream.Stream;
import org.apache.commons.lang3.concurrent.ConcurrentException;

public final class RootMavenModule extends MavenModuleNg {
    public RootMavenModule(File rootPomFile) {
        super(rootPomFile);
    }

    public Stream<ChildMavenModule> children() throws ConcurrentException {
        return effectivePom()
                .modules()
                .map(name -> new ChildMavenModule(
                        getPomFile()
                                .toPath()
                                .getParent()
                                .resolve(name)
                                .resolve("pom.xml")
                                .toFile(),
                        this,
                        name));
    }
}
