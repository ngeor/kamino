package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record MavenCoordinates(String groupId, String artifactId, String version) {
    public MavenCoordinates {
        if (artifactId == null || artifactId.isBlank()) {
            throw new IllegalArgumentException(
                    String.format("%s can never be null or blank, as it cannot be inherited", ARTIFACT_ID));
        }
    }

    public static MavenCoordinates fromElement(ElementWrapper element) {
        Objects.requireNonNull(element);

        Map<String, String> items = element
                .findChildElements(Set.of(GROUP_ID, ARTIFACT_ID, VERSION), e -> e.getTextContentTrimmed()
                        .isPresent())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getTextContentTrimmed().orElseThrow()));

        return new MavenCoordinates(items.get(GROUP_ID), items.get(ARTIFACT_ID), items.get(VERSION));
    }

    public MavenCoordinates requireAllFields() {
        // artifactId is checked at the constructor

        if (groupId == null || groupId.isBlank()) {
            throw new IllegalStateException(String.format("%s is missing (%s=%s)", GROUP_ID, ARTIFACT_ID, artifactId));
        }

        if (version == null || version.isBlank()) {
            throw new IllegalStateException(String.format("%s is missing %s:%s", VERSION, groupId, artifactId));
        }

        return this;
    }

    public MavenCoordinates withVersion(String newVersion) {
        return new MavenCoordinates(groupId, artifactId, newVersion);
    }
}
