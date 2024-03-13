package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public class FilePomDocument extends PomDocument {
    private final File pomFile;

    public FilePomDocument(File pomFile) {
        this.pomFile = Objects.requireNonNull(pomFile);
    }

    @Override
    protected DocumentWrapper doLoadDocument() {
        return DocumentWrapper.parse(pomFile);
    }

    public File getPomFile() {
        return pomFile;
    }
}
