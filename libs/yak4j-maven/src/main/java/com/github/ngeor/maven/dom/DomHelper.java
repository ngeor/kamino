package com.github.ngeor.maven.dom;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.PARENT;
import static com.github.ngeor.maven.ElementNames.RELATIVE_PATH;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DomHelper {
    private DomHelper() {}

    public static MavenCoordinates getCoordinates(DocumentWrapper document) {
        Objects.requireNonNull(document);
        return getCoordinates(document.getDocumentElement());
    }

    public static MavenCoordinates getCoordinates(ElementWrapper element) {
        Objects.requireNonNull(element);
        String[] items = element.firstElementsText(GROUP_ID, ARTIFACT_ID, VERSION);
        return new MavenCoordinates(items[0], items[1], items[2]);
    }

    public static Optional<ParentPom> getParentPom(DocumentWrapper document) {
        Objects.requireNonNull(document);
        return document.getDocumentElement()
                .findChildElements(PARENT)
                .findFirst()
                .map(parentElement -> {
                    String[] items = parentElement.firstElementsText(GROUP_ID, ARTIFACT_ID, VERSION, RELATIVE_PATH);
                    return new ParentPom(new MavenCoordinates(items[0], items[1], items[2]), items[3]);
                });
    }

    public static Stream<MavenCoordinates> getDependencies(DocumentWrapper document) {
        return document.getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(DomHelper::getCoordinates);
    }

    public static Stream<String> getModules(DocumentWrapper document) {
        return document.getDocumentElement()
                .findChildElements("modules")
                .flatMap(e -> e.findChildElements("module"))
                .flatMap(ElementWrapper::getTextContentTrimmedAsStream);
    }

    /**
     * Gets the value of the given property.
     * <p>
     * The result is not trimmed.
     * @param document The Maven document.
     * @param name The property name.
     * @return The value of the given property.
     */
    public static Optional<String> getProperty(DocumentWrapper document, String name) {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(p -> p.findChildElements(name))
                .map(ElementWrapper::getTextContent)
                .findFirst();
    }

    public static Map<String, String> getProperties(DocumentWrapper document) {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(ElementWrapper::getChildElements)
                .collect(Collectors.toMap(ElementWrapper::getNodeName, ElementWrapper::getTextContent));
    }
}
