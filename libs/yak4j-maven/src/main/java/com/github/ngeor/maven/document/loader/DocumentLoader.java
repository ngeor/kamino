package com.github.ngeor.maven.document.loader;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.StringUtils;

public interface DocumentLoader {

    // TODO return an immutable version of DocumentWrapper
    DocumentWrapper loadDocument();

    /**
     * Gets the POM file.
     * This is used to resolve a parent POM via the relative path.
     */
    File getPomFile();

    // TODO move to a different interface where DocumentLoader is a no-op
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

    // TODO make it a decorator, do not actually load the document here
    default DocumentLoader mapDocument(UnaryOperator<DocumentWrapper> mapper) {
        DocumentWrapper document = loadDocument();
        DocumentWrapper mapped = mapper.apply(document);
        return document == mapped ? this : new PreloadedDocumentLoader(mapped, getPomFile());
    }
}
