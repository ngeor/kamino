package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.document.cache.CanonicalFile;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PomDocumentFactory {
    private final Map<CanonicalFile, PomDocument> cache = new HashMap<>();

    public PomDocument create(Path pomFilePath) {
        return create(pomFilePath.toFile());
    }

    public PomDocument create(File pomFilePath) {
        return cache.computeIfAbsent(new CanonicalFile(pomFilePath), k -> new PomDocument(this, k.file()));
    }
}
