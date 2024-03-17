package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class BaseDocument {
    private final PomDocumentFactory owner;
    private final Supplier<DocumentWrapper> document;
    private final Supplier<Optional<ParentPom>> parentPom;

    protected BaseDocument(PomDocumentFactory owner, Supplier<DocumentWrapper> documentLoader) {
        this.owner = Objects.requireNonNull(owner);
        this.document = new Lazy<>(documentLoader, this::notifyOwnerThatDocumentWasLoaded);
        this.parentPom = new Lazy<>(this::doLoadParentPom);
    }

    protected PomDocumentFactory getOwner() {
        return owner;
    }

    public final DocumentWrapper loadDocument() {
        return document.get();
    }

    private void notifyOwnerThatDocumentWasLoaded(DocumentWrapper ignored) {
        owner.documentLoaded(this);
    }

    public MavenCoordinates coordinates() {
        return DomHelper.coordinates(loadDocument());
    }

    protected Optional<ParentPom> parentPom() {
        return parentPom.get();
    }

    private Optional<ParentPom> doLoadParentPom() {
        return DomHelper.getParentPom(loadDocument());
    }

    public Stream<String> modules() {
        return DomHelper.getModules(loadDocument());
    }

    public DocumentWrapper resolveProperties() {
        Map<String, String> unresolvedProperties = DomHelper.getProperties(loadDocument());
        if (unresolvedProperties == null || unresolvedProperties.isEmpty()) {
            return loadDocument();
        }

        // resolve them
        Map<String, String> resolvedProperties = StringPropertyResolver.resolve(unresolvedProperties);
        DocumentWrapper result = loadDocument().deepClone();
        boolean hadChanges = result.getDocumentElement()
                .transformTextNodes(text -> StringPropertyResolver.resolve(text, resolvedProperties::get));
        return hadChanges ? result : loadDocument();
    }
}
