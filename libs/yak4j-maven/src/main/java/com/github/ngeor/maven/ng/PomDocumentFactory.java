package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.document.cache.CanonicalFile;
import com.github.ngeor.maven.document.effective.PomMerger;
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

    public PomDocument create(Path pomFilePath) {
        return create(pomFilePath.toFile());
    }

    public PomDocument create(File pomFilePath) {
        return cache.computeIfAbsent(new CanonicalFile(pomFilePath), k -> new PomDocument(this, k.file()));
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
}
