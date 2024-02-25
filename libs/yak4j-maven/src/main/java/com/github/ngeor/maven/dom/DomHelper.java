package com.github.ngeor.maven.dom;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DomHelper {
    private DomHelper() {}

    public static MavenCoordinates getCoordinates(DocumentWrapper document) {
        Objects.requireNonNull(document);
        return getCoordinates(document.getDocumentElement());
    }

    public static MavenCoordinates getCoordinates(ElementWrapper element) {
        Objects.requireNonNull(element);
        Map<String, String> items = element.firstElementsText(Set.of(GROUP_ID, ARTIFACT_ID, VERSION));
        return new MavenCoordinates(items.get(GROUP_ID), items.get(ARTIFACT_ID), items.get(VERSION));
    }
}
