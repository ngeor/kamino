package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;

public abstract class BasePomDocument {
    private final DocumentWrapper document;

    protected BasePomDocument(DocumentWrapper document) {
        this.document = Objects.requireNonNull(document);
    }

    protected DocumentWrapper getDocument() {
        return document;
    }

    public MavenCoordinates coordinates() {
        return DomHelper.coordinates(document);
    }
}
