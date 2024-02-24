package com.github.ngeor.maven;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.apache.commons.lang3.stream.Streams;

public final class RootMavenModule extends MavenModule {
    private final LazyInitializer<List<ChildMavenModule>> lazyChildren = new LazyInitializer<>() {
        @Override
        protected List<ChildMavenModule> initialize() throws ConcurrentException {
            return effectivePom()
                    .modules()
                    .sorted()
                    .map(RootMavenModule.this::createChild)
                    .toList();
        }
    };

    public RootMavenModule(File rootPomFile) {
        super(rootPomFile);
    }

    public Stream<ChildMavenModule> children() throws ConcurrentException {
        return lazyChildren.get().stream();
    }

    public Stream<ChildMavenModule> children(String groupId, String artifactId) throws ConcurrentException {
        Validate.notBlank(groupId);
        Validate.notBlank(artifactId);
        return Streams.failableStream(children())
                .filter(child -> groupId.equals(child.coordinates().groupId())
                        && artifactId.equals(child.coordinates().artifactId()))
                .stream();
    }

    @Override
    public RootMavenModule getRootParent() {
        return this;
    }

    private ChildMavenModule createChild(String moduleName) {
        return new ChildMavenModule(
                getPomFile()
                        .toPath()
                        .getParent()
                        .resolve(moduleName)
                        .resolve("pom.xml")
                        .toFile(),
                this,
                moduleName);
    }
}
