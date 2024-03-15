package com.github.ngeor.maven.ng;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class BaseDocument {
    private final PomDocumentFactory owner;
    private final Supplier<DocumentWrapper> document;
    private final Supplier<Optional<ParentPom>> parentPom;

    public BaseDocument(PomDocumentFactory owner) {
        this.owner = Objects.requireNonNull(owner);
        this.document = new FnLazy<>(this::doLoadDocument);
        this.parentPom = new FnOptLazy<>(this::doLoadParentPom);
    }

    public final DocumentWrapper loadDocument() {
        return document.get();
    }

    public MavenCoordinates coordinates() {
        return DomHelper.coordinates(loadDocument());
    }

    protected abstract DocumentWrapper doLoadDocument();

    protected PomDocumentFactory getOwner() {
        return owner;
    }

    protected Optional<ParentPom> parentPom() {
        return parentPom.get();
    }

    private Optional<ParentPom> doLoadParentPom() {
        return DomHelper.getParentPom(loadDocument());
    }
}
