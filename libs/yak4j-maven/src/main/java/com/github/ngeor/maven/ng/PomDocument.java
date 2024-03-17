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
    private final Lazy<EffectiveDocument> lazyEffectiveDocument;

    protected PomDocument(PomDocumentFactory owner, File pomFile) {
        super(owner, () -> DocumentWrapper.parse(pomFile));
        this.pomFile = Objects.requireNonNull(pomFile);
        this.lazyEffectiveDocument = new Lazy<>(this::doCreateEffectiveDocument);
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
        return lazyEffectiveDocument.get();
    }

    private EffectiveDocument doCreateEffectiveDocument() {
        PomDocument parentPomDocument = parent().orElse(null);
        return parentPomDocument == null
                ? new EffectiveDocument.Root(this)
                : new EffectiveDocument.Child(parentPomDocument.toEffective(), this);
    }

    public Set<String> internalDependenciesOfModule(String moduleName) {
        Set<String> modules = modules().collect(Collectors.toSet());
        Validate.isTrue(!modules.isEmpty(), "Document is not an aggregator module");
        Validate.isTrue(modules.contains(moduleName), "Module %s is unknown", moduleName);
        Map<String, EffectiveDocument> map = modules.stream()
                .collect(Collectors.toMap(Function.identity(), name -> getOwner()
                        .create(pomFile.toPath().getParent().resolve(name).resolve("pom.xml"))
                        .toEffective()));
        Map<MavenCoordinates, String> coordinatesToModule = map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue().coordinates(), Map.Entry::getKey));

        Graph<String> graph = new Graph<>();
        for (String from : modules) {
            EffectiveDocument effectivePom = map.get(from);
            for (String to : DomHelper.getDependencies(effectivePom.loadDocument())
                    .map(coordinatesToModule::get)
                    .toList()) {
                graph.put(from, to);
            }
        }

        Set<String> result = new HashSet<>();
        graph.visit(moduleName, result::add);
        return result;
    }
}
