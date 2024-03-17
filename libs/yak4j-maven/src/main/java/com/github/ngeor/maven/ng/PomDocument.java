package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;

public class PomDocument extends BaseDocument {
    private final File pomFile;

    protected PomDocument(PomDocumentFactory owner, File pomFile) {
        super(owner, () -> DocumentWrapper.parse(pomFile));
        this.pomFile = Objects.requireNonNull(pomFile);
    }

    public Optional<PomDocument> parent() {
        return parentPom().map(p -> {
            String relativePath = Objects.requireNonNullElse(p.relativePath(), "../pom.xml");
            File parentPomFile =
                    pomFile.toPath().getParent().resolve(relativePath).toFile();
            if (parentPomFile.isDirectory()) {
                parentPomFile = new File(parentPomFile, "pom.xml");
            }
            // TODO try local repository
            return getOwner().create(parentPomFile);
        });
    }

    public EffectiveDocument toEffective() {
        PomDocument parentPomDocument = parent().orElse(null);
        return parentPomDocument == null
                ? new EffectiveDocument.Root(this)
                : new EffectiveDocument.Child(parentPomDocument.toEffective(), this);
    }

    public Set<String> internalDependenciesOfModule(String moduleName) {
        Graph<String> graph = createDependencyGraph();
        Set<String> result = new HashSet<>();
        graph.visit(moduleName, result::add);
        return result;
    }

    protected Graph<String> createDependencyGraph() {
        ModuleMap moduleMap = new ModuleMap();
        Graph<String> graph = new Graph<>();
        for (String from : moduleMap.getModules()) {
            EffectiveDocument effectivePom = moduleMap.get(from).toEffective();
            for (String to : DomHelper.getDependencies(effectivePom.loadDocument())
                    .map(moduleMap::moduleByCoordinates)
                    .filter(Objects::nonNull)
                    .toList()) {
                graph.put(from, to);
            }
        }
        return graph;
    }

    public Set<String> ancestorsOfModule(String moduleName) {
        Graph<String> graph = createAncestorsGraph();
        Set<String> result = new HashSet<>();
        graph.visit(moduleName, result::add);
        return result;
    }

    protected Graph<String> createAncestorsGraph() {
        ModuleMap moduleMap = new ModuleMap();
        Graph<String> graph = new Graph<>();
        for (String from : moduleMap.getModules()) {
            PomDocument pomDocument = moduleMap.get(from);
            pomDocument
                    .parent()
                    .map(BaseDocument::coordinates)
                    .map(moduleMap::moduleByCoordinates)
                    .ifPresent(to -> graph.put(from, to));
        }
        return graph;
    }

    public PomDocument loadModule(String moduleName) {
        Objects.requireNonNull(moduleName);
        Validate.notBlank(moduleName);
        return getOwner()
                .create(pomFile.toPath().getParent().resolve(moduleName).resolve("pom.xml"));
    }

    private class ModuleMap {
        private final Map<String, PomDocument> map;
        private final Map<MavenCoordinates, String> coordinatesToModule;

        public ModuleMap() {
            Set<String> modules = modules().collect(Collectors.toSet());
            Validate.isTrue(!modules.isEmpty(), "Document is not an aggregator module");
            this.map = modules.stream().collect(Collectors.toMap(Function.identity(), PomDocument.this::loadModule));
            this.coordinatesToModule = mapCoordinatesToModule(map);
        }

        public Set<String> getModules() {
            return map.keySet();
        }

        public PomDocument get(String moduleName) {
            return map.get(moduleName);
        }

        public String moduleByCoordinates(MavenCoordinates mavenCoordinates) {
            return coordinatesToModule.get(mavenCoordinates);
        }

        private static Map<MavenCoordinates, String> mapCoordinatesToModule(Map<String, ? extends BaseDocument> map) {
            return map.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getValue().coordinates(), Map.Entry::getKey));
        }
    }
}
