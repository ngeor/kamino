package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public abstract class DocumentLoaderDecorator implements DocumentLoader {
    private final DocumentLoader decorated;

    protected DocumentLoaderDecorator(DocumentLoader decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return decorated.loadDocument();
    }

    @Override
    public File getPomFile() {
        return decorated.getPomFile();
    }

    @Override
    public String toString() {
        return String.format("%s %s", getClass().getSimpleName(), decorated);
    }

    protected DocumentLoader getDecorated() {
        return decorated;
    }
}
