package com.github.ngeor;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParent;
import com.github.ngeor.maven.document.parent.ParentDocumentLoaderIterator;
import com.github.ngeor.maven.document.property.CanResolveProperties;
import com.github.ngeor.maven.document.repository.PomRepository;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.HasVersion;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ModuleRepository {
    private final PomRepository pomRepository = new PomRepository();
    private List<String> modules;
    private Path aggregatorFile;

    public void loadModules(Path aggregatorFile) {
        this.aggregatorFile = Objects.requireNonNull(aggregatorFile);
        DocumentLoader documentLoader = pomRepository.createDocumentLoader(aggregatorFile);
        DocumentWrapper document = documentLoader.loadDocument();
        modules = DomHelper.getModules(document).toList();
    }

    public List<String> moduleNames() {
        return modules;
    }

    public CanResolveProperties moduleDocumentLoader(String name) {
        return pomRepository.createDocumentLoader(
                aggregatorFile.getParent().resolve(name).resolve("pom.xml"));
    }

    public MavenCoordinates moduleCoordinates(String name) {
        DocumentWrapper document = moduleDocumentLoader(name).loadDocument();
        return DomHelper.coordinates(document);
    }

    public Optional<String> moduleByCoordinates(MavenCoordinates coordinates) {
        return moduleNames().stream()
                .filter(name -> coordinates.equals(moduleCoordinates(name)))
                .findFirst();
    }

    public Set<String> dependenciesOf(String name) {
        DocumentWrapper document = moduleDocumentLoader(name).resolveProperties();
        return DomHelper.getDependencies(document)
                .flatMap(dep -> moduleByCoordinates(dep).stream())
                .collect(Collectors.toSet());
    }

    public Set<String> dependenciesOfRecursively(String name) {
        return dependenciesOfRecursively(name, new HashSet<>());
    }

    private Set<String> dependenciesOfRecursively(String name, Set<String> visited) {
        if (!visited.add(name)) {
            return Collections.emptySet();
        }
        Set<String> oneLevel = dependenciesOf(name);
        Set<String> result = new LinkedHashSet<>(oneLevel);
        for (String other : oneLevel) {
            result.addAll(dependenciesOfRecursively(other, visited));
        }
        return result;
    }

    public Set<String> parentSnapshotsOfRecursively(String name) {
        ParentDocumentLoaderIterator it = new ParentDocumentLoaderIterator(moduleDocumentLoader(name));
        Iterable<CanLoadParent> iterable = () -> it;
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(DocumentLoader::loadDocument)
                .map(DomHelper::coordinates)
                .filter(HasVersion::isSnapshot)
                .flatMap(c -> moduleByCoordinates(c).stream())
                .collect(Collectors.toSet());
    }
}
