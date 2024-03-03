package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public final class PreloadedDocumentLoader implements DocumentLoader {
    private final DocumentWrapper document;
    private final File pomFile;

    public PreloadedDocumentLoader(DocumentWrapper document, File pomFile) {
        this.document = Objects.requireNonNull(document);
        this.pomFile = Objects.requireNonNull(pomFile);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return document;
    }

    @Override
    public File getPomFile() {
        return pomFile;
    }

    @Override
    public String toString() {
        return String.format(
                "%s pomFile=%s document=%s", PreloadedDocumentLoader.class.getSimpleName(), pomFile, document);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PreloadedDocumentLoader other
                && Objects.equals(document, other.document)
                && Objects.equals(pomFile, other.pomFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, pomFile);
    }
}
