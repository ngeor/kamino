package com.github.ngeor.yak4jcli;

import com.github.ngeor.yak4jdom.ElementWrapper;

/**
 * A wrapper around a {@code dependency} pom element.
 */
public class PomDependencyElement implements HasCoordinates {
    private final ElementWrapper element;

    public PomDependencyElement(ElementWrapper element) {
        this.element = element;
    }

    @Override
    public String getGroupId() {
        return element.firstElementText("groupId");
    }

    @Override
    public String getArtifactId() {
        return element.firstElementText("artifactId");
    }

    public String getVersion() {
        return element.firstElementText("version");
    }

    /**
     * Sets the version of the dependency.
     */
    public void setVersion(String version) {
        ElementWrapper versionElement = element.firstElement("version").orElseThrow(
            () -> new IllegalArgumentException("Could not find version element")
        );
        versionElement.setTextContent(version);
    }
}
