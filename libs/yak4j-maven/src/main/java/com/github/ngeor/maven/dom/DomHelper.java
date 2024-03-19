package com.github.ngeor.maven.dom;

import static com.github.ngeor.maven.dom.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.dom.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.dom.ElementNames.PARENT;
import static com.github.ngeor.maven.dom.ElementNames.RELATIVE_PATH;
import static com.github.ngeor.maven.dom.ElementNames.VERSION;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Deprecated
public final class DomHelper {
    private DomHelper() {}

    private static MavenCoordinates getCoordinates(ElementWrapper element) {
        Objects.requireNonNull(element);
        String[] items = element.firstElementsText(GROUP_ID, ARTIFACT_ID, VERSION);
        return new MavenCoordinates(items[0], items[1], items[2]);
    }

    public static Optional<ParentPom> getParentPom(DocumentWrapper document) {
        Objects.requireNonNull(document);
        return document.getDocumentElement()
                .findChildElements(PARENT)
                .findFirst()
                .map(DomHelper::getParentPom);
    }

    public static ParentPom getParentPom(ElementWrapper parentElement) {
        Objects.requireNonNull(parentElement);
        String[] items = parentElement.firstElementsText(GROUP_ID, ARTIFACT_ID, VERSION, RELATIVE_PATH);
        return new ParentPom(items[0], items[1], items[2], items[3]);
    }

    public static Stream<MavenCoordinates> getDependencies(DocumentWrapper document) {
        return document.getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(DomHelper::getCoordinates);
    }
}
