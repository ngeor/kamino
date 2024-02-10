package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public final class MavenModules {
    private final File root;
    private List<MavenModule> modules;

    public MavenModules(File root) {
        this.root = root;
    }

    public List<MavenModule> getModules() {
        if (modules == null) {
            modules = collectModules();
        }
        return modules;
    }

    private List<MavenModule> collectModules() {
        List<MavenModule> modules = new ArrayList<>();
        for (File typeDirectory : getDirectories(root)) {
            for (File projectDirectory : getDirectories(typeDirectory)) {
                File pomFile = new File(projectDirectory, "pom.xml");
                if (pomFile.isFile()) {
                    MavenModule module = new MavenModule(typeDirectory, projectDirectory, pomFile);
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    private static File[] getDirectories(File file) {
        return file.listFiles(new DirectoryFileFilter());
    }

    public Stream<MavenModule> internalDependencies(MavenModule module)
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        return module.dependencies().flatMap(c -> internalDependency(c).stream());
    }

    private Optional<MavenModule> internalDependency(MavenCoordinates coordinates) {
        return getModules().stream()
                .filter(m -> {
                    try {
                        return coordinates.equals(m.coordinates());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst();
    }

    public void visitDependenciesRecursively(MavenModule module, Consumer<MavenModule> visitor)
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
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