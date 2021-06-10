package com.github.ngeor.yak4jcli;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.stream.Stream;

/**
 * Wrapper for an XML Document describing a pom file.
 */
public class PomDocument {
    private final DocumentWrapper document;

    public PomDocument(DocumentWrapper document) {
        this.document = document;
    }

    public static PomDocument parse(File file) {
        return new PomDocument(DocumentWrapper.parse(file));
    }

    public String getGroupId() {
        return document.getDocumentElement().firstElementText("groupId");
    }

    public String getArtifactId() {
        return document.getDocumentElement().firstElementText("artifactId");
    }

    public String getVersion() {
        return document.getDocumentElement().firstElementText("version");
    }

    public Stream<ElementWrapper> getModules() {
        return document.getDocumentElement().findChildElements("modules").flatMap(
            modules -> modules.findChildElements("module")
        );
    }

    public String getPackaging() {
        return document.getDocumentElement().firstElementText("packaging");
    }
}
