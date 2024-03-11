package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import java.util.Optional;

public class PomDocument {
    private final DocumentWrapper document;

    public PomDocument(DocumentWrapper document) {
        this.document = Objects.requireNonNull(document);
    }

    public MavenCoordinates coordinates() {
        return DomHelper.coordinates(document);
    }

    public Optional<ParentPom> parentPom() {
        return DomHelper.getParentPom(document);
    }

    public Optional<PomDocument> parent(Repository repository) {
        return parentPom().map(repository::load).map(PomDocument::new);
    }
}
