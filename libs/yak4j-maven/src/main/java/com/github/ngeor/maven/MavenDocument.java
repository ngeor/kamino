package com.github.ngeor.maven;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MavenDocument {
    private final DocumentWrapper document;

    public MavenDocument(DocumentWrapper document) {
        this.document = Objects.requireNonNull(document);
    }

    public ParentPom parentPom() {
        return ParentPom.fromDocument(document).orElse(null);
    }

    public MavenCoordinates coordinates() {
        return MavenCoordinates.fromElement(document.getDocumentElement());
    }

    public Stream<MavenCoordinates> dependencies() {
        return document.getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(MavenCoordinates::fromElement);
    }

    public Stream<String> modules() {
        return document.getDocumentElement()
                .findChildElements("modules")
                .flatMap(e -> e.findChildElements("module"))
                .flatMap(ElementWrapper::getTextContentTrimmedAsStream);
    }

    public Map<String, String> properties() {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(ElementWrapper::getChildElements)
                .collect(Collectors.toMap(ElementWrapper::getNodeName, ElementWrapper::getTextContent));
    }

    public Optional<String> property(String name) {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(p -> p.findChildElements(name))
                .flatMap(ElementWrapper::getTextContentTrimmedAsStream)
                .findFirst();
    }

    public Optional<String> modelVersion() {
        return topLevelElement("modelVersion");
    }

    public Optional<String> name() {
        return topLevelElement("name");
    }

    private Optional<String> topLevelElement(String childElementName) {
        return document.getDocumentElement()
                .findChildElements(childElementName)
                .flatMap(ElementWrapper::getTextContentTrimmedAsStream)
                .findFirst();
    }
}
