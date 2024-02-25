package com.github.ngeor.maven.resolve;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.dom.DomHelper;
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

    public MavenCoordinates coordinates() {
        return DomHelper.getCoordinates(document);
    }

    public Stream<MavenCoordinates> dependencies() {
        return document.getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(DomHelper::getCoordinates);
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
