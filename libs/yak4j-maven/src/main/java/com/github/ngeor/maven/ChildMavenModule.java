package com.github.ngeor.maven;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.stream.Streams;

public final class ChildMavenModule extends MavenModule {
    private final MavenModule parent;
    private final String moduleName;

    public ChildMavenModule(File pomFile, MavenModule parent, String moduleName) {
        super(pomFile);
        this.parent = Objects.requireNonNull(parent);
        this.moduleName = Validate.notBlank(moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public RootMavenModule getRootParent() {
        return parent.getRootParent();
    }

    public Stream<ChildMavenModule> internalDependencies() throws ConcurrentException {
        return Streams.failableStream(dependencies())
                .map(dependencyCoordinates ->
                        getRootParent().children(dependencyCoordinates.groupId(), dependencyCoordinates.artifactId()))
                .stream()
                .flatMap(Function.identity());
    }

    public Stream<ChildMavenModule> internalDependenciesRecursively() throws ConcurrentException {
        // TODO prevent stack overflow due to circular dependencies
        List<ChildMavenModule> oneLevel = internalDependencies().toList();
        return Stream.concat(
                oneLevel.stream(),
                Streams.failableStream(oneLevel).map(ChildMavenModule::internalDependenciesRecursively).stream()
                        .flatMap(Function.identity()));
    }
}
