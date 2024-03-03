package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public record FileDocumentLoader(File pomFile) implements DocumentLoader {
    public FileDocumentLoader {
        Objects.requireNonNull(pomFile);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return DocumentWrapper.parse(pomFile);
    }

    @Override
    public File getPomFile() {
        return pomFile;
    }

    public static DocumentLoaderFactory<DocumentLoader> asFactory() {
        return FileDocumentLoader::new;
    }
}
