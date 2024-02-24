package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public record MavenCoordinates(String groupId, String artifactId, String version) {
    public static MavenCoordinates fromElement(ElementWrapper element) {
        Objects.requireNonNull(element);

        Map<String, String> items = element.firstElementsText(Set.of(GROUP_ID, ARTIFACT_ID, VERSION));
        return new MavenCoordinates(items.get(GROUP_ID), items.get(ARTIFACT_ID), items.get(VERSION));
    }

    public boolean hasMissingFields() {
        return StringUtils.isAnyBlank(groupId, artifactId, version);
    }

    public MavenCoordinates requireAllFields() {
        Validate.notBlank(groupId, "%s is missing (%s=%s)", GROUP_ID, ARTIFACT_ID, artifactId);
        Validate.notBlank(artifactId, "%s can never be null or blank, as it cannot be inherited", ARTIFACT_ID);
        Validate.notBlank(version, "%s is missing %s:%s", VERSION, groupId, artifactId);
        return this;
    }

    public MavenCoordinates withVersion(String newVersion) {
        return new MavenCoordinates(groupId, artifactId, newVersion);
    }

    public String format() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }

    @Override
    public String toString() {
        return String.format("%s %s", MavenCoordinates.class.getSimpleName(), format());
    }
}
