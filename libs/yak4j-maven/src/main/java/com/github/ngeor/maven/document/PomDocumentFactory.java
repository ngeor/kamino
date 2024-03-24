package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class PomDocumentFactory {
    private final Map<CanonicalFile, PomDocument> cache = new HashMap<>();
    private final List<BiConsumer<MavenCoordinates, MavenCoordinates>> mergeListeners = new ArrayList<>();
    private final Map<MavenCoordinates, Map<MavenCoordinates, DocumentWrapper>> merges = new HashMap<>();
    private final Map<MavenCoordinates, PomDocument> loadedDocuments = new HashMap<>();

    public PomDocument create(Path pomFilePath) {
        return create(pomFilePath.toFile());
    }

    public PomDocument create(Path rootPath, String relativePath) {
        String path = relativePath.endsWith("/") ? relativePath + "pom.xml" : relativePath;
        return create(rootPath.resolve(path));
    }

    public PomDocument create(File pomFilePath) {
        return cache.computeIfAbsent(new CanonicalFile(pomFilePath), k -> new CachePomDocument(this, k.file()));
    }

    public void addMergeListener(BiConsumer<MavenCoordinates, MavenCoordinates> listener) {
        mergeListeners.add(listener);
    }

    protected DocumentWrapper merge(BaseDocument left, BaseDocument right) {
        return merges.computeIfAbsent(left.coordinates(), leftKey -> new HashMap<>())
                .computeIfAbsent(right.coordinates(), rightKey -> {
                    DocumentWrapper merged =
                            PomMerger.mergeIntoLeft(left.loadDocument().deepClone(), right.loadDocument());
                    mergeListeners.forEach(listener -> listener.accept(left.coordinates(), right.coordinates()));
                    return merged;
                });
    }

    public PomDocument moduleByCoordinates(MavenCoordinates coordinates) {
        return loadedDocuments.get(coordinates);
    }

    protected void documentLoaded(BaseDocument document) {
        if (document instanceof PomDocument pomDocument) {
            loadedDocuments.put(pomDocument.coordinates(), pomDocument);
        }
    }
}
