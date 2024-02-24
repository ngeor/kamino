package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.PARENT;
import static com.github.ngeor.maven.ElementNames.RELATIVE_PATH;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record ParentPom(MavenCoordinates coordinates, String relativePath) {
    public static Optional<ParentPom> fromDocument(DocumentWrapper document) {
        return Optional.ofNullable(document).map(DocumentWrapper::getDocumentElement).stream()
                .flatMap(ElementWrapper::getChildElements)
                .filter(e -> PARENT.equals(e.getNodeName()))
                .map(ParentPom::fromParentElement)
                .findFirst();
    }

    private static ParentPom fromParentElement(ElementWrapper parentElement) {
        Objects.requireNonNull(parentElement);
        Map<String, String> items = parentElement
                .findChildElements(Set.of(GROUP_ID, ARTIFACT_ID, VERSION, RELATIVE_PATH), e -> e.getTextContentTrimmed()
                        .isPresent())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getTextContentTrimmed().orElseThrow()));

        return new ParentPom(
                new MavenCoordinates(items.get(GROUP_ID), items.get(ARTIFACT_ID), items.get(VERSION)),
                items.get(RELATIVE_PATH));
    }
}
