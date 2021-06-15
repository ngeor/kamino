package com.github.ngeor.yak4jcli;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;

/**
 * Wrapper for an XML Document describing a pom file.
 */
public class PomDocument implements HasCoordinates {
    private final DocumentWrapper document;
    private final File file;

    /**
     * Creates an instance of this class.
     */
    public PomDocument(DocumentWrapper document, File file) {
        Validate.notNull(document, "Document cannot be null");
        Validate.notNull(file, "File cannot be null");
        this.document = document;
        this.file = file;
    }

    public static PomDocument parse(File file) {
        return new PomDocument(DocumentWrapper.parse(file), file);
    }

    public void write() {
        document.write(file);
    }

    @Override
    public String getGroupId() {
        String groupId = document.getDocumentElement().firstElementText("groupId");
        if (groupId == null) {
            return getParent().map(PomParentElement::getGroupId).orElse(null);
        } else {
            return groupId;
        }
    }

    @Override
    public String getArtifactId() {
        return document.getDocumentElement().firstElementText("artifactId");
    }

    /**
     * Gets the version of this pom document.
     */
    public String getVersion() {
        String version = document.getDocumentElement().firstElementText("version");
        if (version == null) {
            return getParent().map(PomParentElement::getVersion).orElse(null);
        } else {
            return version;
        }
    }

    public Stream<ElementWrapper> getModules() {
        return document.getDocumentElement().findChildElements("modules").flatMap(
            modules -> modules.findChildElements("module")
        );
    }

    public String getPackaging() {
        return document.getDocumentElement().firstElementText("packaging");
    }

    public Optional<PomParentElement> getParent() {
        return document.getDocumentElement().firstElement("parent").map(PomParentElement::new);
    }

    public Stream<PomDependencyElement> getDependencies() {
        return document.getDocumentElement().findChildElements("dependencies").flatMap(
            dependencies -> dependencies.findChildElements("dependency").map(PomDependencyElement::new)
        );
    }

    /**
     * Sets the version of the pom document.
     */
    public void setVersion(String version) {
        ElementWrapper versionElement = document.getDocumentElement().firstElement("version").orElseThrow(
            () -> new IllegalArgumentException("Could not find version element")
        );
        versionElement.setTextContent(version);
    }

    public File getFile() {
        return this.file;
    }
}
