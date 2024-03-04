package com.github.ngeor.maven.document.loader;

import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * An object that can load a Maven POM document from a file.
 */
public interface DocumentLoader {

    // TODO return an immutable version of DocumentWrapper
    DocumentWrapper loadDocument();

    /**
     * Gets the POM file.
     * This is used to resolve a parent POM via the relative path.
     */
    File getPomFile();

    default MavenCoordinates coordinates() {
        MavenCoordinates coordinates = DomHelper.getCoordinates(loadDocument());
        if (coordinates.isValid()) {
            return coordinates;
        }
        if (StringUtils.isBlank(coordinates.artifactId())) {
            throw new IllegalArgumentException("Cannot resolve coordinates, artifactId is missing");
        }
        MavenCoordinates parentCoordinates = DomHelper.getParentPom(loadDocument())
                .map(ParentPom::validateCoordinates)
                .orElseThrow(
                        () -> new IllegalArgumentException("Cannot resolve coordinates, parent element is missing"));
        return new MavenCoordinates(
                StringUtils.defaultIfBlank(coordinates.groupId(), parentCoordinates.groupId()),
                coordinates.artifactId(),
                StringUtils.defaultIfBlank(coordinates.version(), parentCoordinates.version()));
    }
}
