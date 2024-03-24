package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.maven.find.States;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

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
        Iterator<ElementWrapper> it = loadDocument().getDocumentElement().getChildElementsAsIterator();
        var state = States.mavenCoordinates().compose(
            States.element(ElementNames.PARENT),
            Pair::of
        );

        while (it.hasNext()) {
            if (state.isFound()) {
                // all things are found
                break;
            }
            if (!state.getValue().getLeft().hasMissingFields()) {
                // all coordinates provided, parent element not needed
                break;
            }
            state = state.visit(it.next());
        }

        Pair<MavenCoordinates, ElementWrapper> finderResult = state.getValue();
        MavenCoordinates coordinates = finderResult.getLeft();
        ElementWrapper parent = finderResult.getRight();

        // artifactId is not inherited
        Validate.notBlank(coordinates.artifactId(), "Cannot resolve coordinates, artifactId is missing");
        if (parent == null) {
            Validate.notBlank(coordinates.groupId(), "Cannot resolve coordinates, groupId is missing");
            Validate.notBlank(coordinates.version(), "Cannot resolve coordinates, version is missing");
            return coordinates;
        } else {
            MavenCoordinates parentCoordinates = States.parentPom()
                    .consume(parent.getChildElementsAsIterator())
                    .validateCoordinates();
            return new MavenCoordinates(
                    StringUtils.defaultIfBlank(coordinates.groupId(), parentCoordinates.groupId()),
                    coordinates.artifactId(),
                    StringUtils.defaultIfBlank(coordinates.version(), parentCoordinates.version()));
        }
    }

    protected Optional<ParentPom> parentPom() {
        return parentPom.get();
    }

    private Optional<ParentPom> doLoadParentPom() {
        return loadDocument()
                .getDocumentElement()
                .firstElement(ElementNames.PARENT)
                .map(ElementWrapper::getChildElementsAsIterator)
                .map(States.parentPom()::consume);
    }

    public Stream<String> modules() {
        return loadDocument()
                .getDocumentElement()
                .findChildElements("modules")
                .flatMap(e -> e.findChildElements("module"))
                .flatMap(ElementWrapper::getTextContentTrimmedAsStream);
    }

    public Optional<String> getProperty(String propertyName) {
        return loadDocument()
                .getDocumentElement()
                .findChildElements("properties")
                .flatMap(p -> p.findChildElements(propertyName))
                .flatMap(ElementWrapper::getTextContentTrimmedAsStream)
                .findFirst();
    }

    public ResolvedPropertiesDocument toResolvedProperties() {
        return new ResolvedPropertiesDocument(this);
    }

    public Stream<MavenCoordinates> dependencies() {
        return loadDocument()
                .getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(ElementWrapper::getChildElementsAsIterator)
                .map(States.mavenCoordinates()::consume);
    }
}
