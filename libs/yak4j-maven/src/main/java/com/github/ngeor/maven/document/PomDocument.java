package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.InputStream;
import java.util.Objects;

public class PomDocument {
    private final DocumentWrapper document;

    public PomDocument(DocumentWrapper document) {
        this.document = Objects.requireNonNull(document);
    }

    public PomDocument(InputStream inputStream) {
        this(DocumentWrapper.parse(inputStream));
    }

    public MavenCoordinates coordinates() {
        return DomHelper.coordinates(document);
    }
}
