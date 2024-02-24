package com.github.ngeor;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ModuleFinder;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;

public final class MavenModules {
    private final File root;
    private SortedSet<MavenModule> modules;

    public MavenModules(File root) {
        this.root = root;
    }

    public SortedSet<MavenModule> getModules() {
        if (modules == null) {
            modules = collectModules();
        }
        return modules;
    }

    private SortedSet<MavenModule> collectModules() {
        return new ModuleFinder().findModules(root.toPath().resolve("pom.xml").toFile())
            .map(moduleName -> moduleName.split("/"))
            .filter(parts -> parts.length == 2)
            .map(parts -> {
                File typeDirectory = root.toPath().resolve(parts[0]).toFile();
                File projectDirectory = typeDirectory.toPath().resolve(parts[1]).toFile();
                File pomFile = projectDirectory.toPath().resolve("pom.xml").toFile();
                return new MavenModule(typeDirectory, projectDirectory, pomFile);
            }).collect(Collectors.toCollection(TreeSet::new));
    }

    public Stream<MavenModule> internalDependencies(MavenModule module) {
        return module.dependencies().flatMap(c -> internalDependency(c).stream());
    }

    private Optional<MavenModule> internalDependency(MavenCoordinates coordinates) {
        return getModules().stream()
                .filter(m -> coordinates.equals(m.coordinates()))
                .findFirst();
    }

    public void visitDependenciesRecursively(MavenModule module, Consumer<MavenModule> visitor) {
        MavenCoordinates ownCoordinates = module.coordinates();
        Set<MavenCoordinates> seen = new HashSet<>();
        LinkedList<MavenModule> remaining = new LinkedList<>(List.of(module));
        while (!remaining.isEmpty()) {
            MavenModule next = remaining.removeFirst();
            MavenCoordinates nextCoordinates = next.coordinates();
            if (seen.add(nextCoordinates)) {
                // visit
                if (!nextCoordinates.equals(ownCoordinates)) {
                    visitor.accept(next);
                }

                // get dependencies
                internalDependencies(next).forEach(remaining::addLast);
            }
        }
    }
}
