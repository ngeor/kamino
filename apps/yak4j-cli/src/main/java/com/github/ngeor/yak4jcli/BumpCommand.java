package com.github.ngeor.yak4jcli;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import picocli.CommandLine;

/**
 * Bumps the version of all modules.
 * Supports bumping into release version and snapshot version.
 */
@CommandLine.Command(name = "bump", description = "Supports bumping into release version and snapshot version")
public class BumpCommand implements Runnable {
    @CommandLine.Option(names = { "-s", "--snapshot" }, description = "Use snapshot versions")
    private boolean snapshot;

    @CommandLine.Option(
        names = { "-v", "--version" },
        description = "How to bump the version (one of ${COMPLETION-CANDIDATES})",
        required = true)
    private SemVerBump semVerBump;

    private Map<String, PomDocument> dirtyDocuments;

    private Map<String, PomDocument> documents;

    @Override
    public void run() {
        preloadModules();
        // 1. validate all dependencies point to the correct versions
        // e.g. the dependency in yak4j-cli of yak4j-dom matches the version declared in yak4j-dom
        // 2. bump all versions (update dependencies too)
        // TODO: how to bump only changed libraries
        dirtyDocuments = new HashMap<>();
        for (PomDocument pomDocument : documents.values()) {
            String oldVersion = pomDocument.getVersion();
            String newVersion = bump(oldVersion);
            pomDocument.setVersion(newVersion);
            markDocumentDirty(pomDocument);
            for (PomDocument otherDocument : documents.values()) {
                if (!pomDocument.getFile().equals(otherDocument.getFile())) {
                    update(otherDocument, pomDocument, newVersion);
                }
            }
        }
        for (PomDocument doc : dirtyDocuments.values()) {
            doc.write();
        }
    }

    private void preloadModules() {
        documents = new HashMap<>();
        Path rootPath = Paths.get(".");
        preloadModule(rootPath);
    }

    private void preloadModule(Path directory) {
        File pomFile = directory.resolve("pom.xml").toFile();
        String key = pomFile.getAbsolutePath();
        PomDocument pomDocument = PomDocument.parse(pomFile);
        documents.put(key, pomDocument);

        pomDocument.getModules().forEach(
            moduleElement -> {
                String moduleName = moduleElement.getTextContent();
                Path modulePath = directory.resolve(moduleName);
                // recurse
                preloadModule(modulePath);
            }
        );
    }

    private void markDocumentDirty(PomDocument document) {
        String file = document.getFile().getAbsolutePath();
        dirtyDocuments.put(file, document);
    }

    private void update(PomDocument pomDocument, HasCoordinates updatedArtifact, String newVersion) {
        boolean dependenciesChanged = updateDependencyReferences(pomDocument, updatedArtifact, newVersion);
        boolean parentPomChanged = updateParentPomReferences(pomDocument, updatedArtifact, newVersion);
        if (dependenciesChanged || parentPomChanged) {
            markDocumentDirty(pomDocument);
        }
    }

    private boolean updateDependencyReferences(
        PomDocument pomDocument, HasCoordinates updatedArtifact, String newVersion) {
        return pomDocument.getDependencies().mapToInt(
            dependency -> {
                if (dependency.matchesGroupArtifact(updatedArtifact)) {
                    dependency.setVersion(newVersion);
                    return 1;
                } else {
                    return 0;
                }
            }
        ).sum() > 0;
    }

    private boolean updateParentPomReferences(PomDocument pomDocument, HasCoordinates artifact, String newVersion) {
        Optional<PomParentElement> optionalParent = pomDocument.getParent();
        if (optionalParent.isPresent()) {
            PomParentElement parentElement = optionalParent.get();
            if (parentElement.matchesGroupArtifact(artifact)) {
                parentElement.setVersion(newVersion);
                return true;
            }
        }
        return false;
    }

    private String bump(String version) {
        return SemVerUtil.bump(version);
    }
}
