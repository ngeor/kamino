package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;

public abstract class BasePomDocument {
    private DocumentWrapper document;

    public final DocumentWrapper loadDocument() {
        if (document == null) {
            document = doLoadDocument();
        }
        return document;
    }

    protected abstract DocumentWrapper doLoadDocument();

    public final MavenCoordinates coordinates() {
        return DomHelper.coordinates(loadDocument());
    }
}
