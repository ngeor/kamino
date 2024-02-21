package com.github.ngeor.maven;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MavenDocument {
    private final DocumentWrapper document;

    MavenDocument(DocumentWrapper document) {
        this.document = document;
    }

    MavenDocument(File pomFile) {
        this(DocumentWrapper.parse(pomFile));
    }

    MavenDocument(String contents) {
        this(DocumentWrapper.parseString(contents));
    }

    @Deprecated
    public DocumentWrapper getDocument() {
        return document;
    }

    @Deprecated
    public ElementWrapper getDocumentElement() {
        return document.getDocumentElement();
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
                .flatMap(e -> e.getTextContentTrimmed().stream());
    }

    public void removeParentPom() {
        document.getDocumentElement().removeChildNodesByName("parent");
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
                .flatMap(p -> p.getTextContentTrimmed().stream())
                .findFirst();
    }

    public void resolveProperties(Map<String, String> resolvedProperties) {
        resolveProperties(document.getDocumentElement(), resolvedProperties);
    }

    private void resolveProperties(ElementWrapper element, Map<String, String> resolvedProperties) {
        for (Iterator<Node> it = element.getChildNodesAsIterator(); it.hasNext(); ) {
            Node node = it.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                resolveProperties(new ElementWrapper((Element) node), resolvedProperties);
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                node.setTextContent(PropertyResolver.resolve(node.getTextContent(), resolvedProperties::get));
            }
        }
    }
}
