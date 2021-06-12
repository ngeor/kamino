package com.github.ngeor.yak4jcli;

import com.github.ngeor.yak4jdom.ElementWrapper;

/**
 * A wrapper around the {@code parent} element of a pom file.
 */
public class PomParentElement {
    private final ElementWrapper element;

    public PomParentElement(ElementWrapper element) {
        this.element = element;
    }

    public String getGroupId() {
        return element.firstElementText("groupId");
    }

    public String getArtifactId() {
        return element.firstElementText("artifactId");
    }

    public String getVersion() {
        return element.firstElementText("version");
    }
}
