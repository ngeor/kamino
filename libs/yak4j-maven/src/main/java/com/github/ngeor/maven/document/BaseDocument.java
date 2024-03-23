package com.github.ngeor.maven.document;

import static com.github.ngeor.maven.dom.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.dom.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.dom.ElementNames.PARENT;
import static com.github.ngeor.maven.dom.ElementNames.VERSION;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.Finders;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

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
//        Finders.mavenCoordinates()
//            .compose(
//                Finders.el
//            )
        ElementFinder<String> groupId = ElementFinder.textFinder(GROUP_ID);
        ElementFinder<String> artifactId = ElementFinder.textFinder(ARTIFACT_ID);
        ElementFinder<String> version = ElementFinder.textFinder(VERSION);
        ElementFinder<ElementWrapper> parent = ElementFinder.elementFinder(PARENT);
        Predicate<ElementWrapper> finders = groupId.or(artifactId).or(version).or(parent);
        while (it.hasNext() && (groupId.isEmpty() || artifactId.isEmpty() || version.isEmpty())) {
            ElementWrapper next = it.next();
            finders.test(next);
        }
        // artifactId is not inherited
        Validate.isTrue(artifactId.isPresent(), "Cannot resolve coordinates, artifactId is missing");
        if (parent.isEmpty()) {
            Validate.isTrue(groupId.isPresent(), "Cannot resolve coordinates, groupId is missing");
            Validate.isTrue(version.isPresent(), "Cannot resolve coordinates, version is missing");
            return new MavenCoordinates(groupId.getValue(), artifactId.getValue(), version.getValue());
        } else {
            MavenCoordinates parentCoordinates =
                    DomHelper.getParentPom(parent.getValue()).validateCoordinates();
            return new MavenCoordinates(
                    StringUtils.defaultIfBlank(groupId.getValue(), parentCoordinates.groupId()),
                    artifactId.getValue(),
                    StringUtils.defaultIfBlank(version.getValue(), parentCoordinates.version()));
        }
    }

    protected Optional<ParentPom> parentPom() {
        return parentPom.get();
    }

    private Optional<ParentPom> doLoadParentPom() {
        return DomHelper.getParentPom(loadDocument());
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
                .map(dependency -> {
                    String[] items = dependency.firstElementsText(GROUP_ID, ARTIFACT_ID, VERSION);
                    return new MavenCoordinates(items[0], items[1], items[2]);
                });
    }
}
