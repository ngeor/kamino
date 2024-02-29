package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.StringUtils;

public interface Input {

    // TODO return an immutable version of DocumentWrapper
    DocumentWrapper document();

    /**
     * Gets the POM file.
     * This is used only to resolve a parent POM via the relative path.
     */
    File pomFile();

    default MavenCoordinates coordinates() {
        MavenCoordinates coordinates = DomHelper.getCoordinates(document());
        if (coordinates.isValid()) {
            return coordinates;
        }
        if (StringUtils.isBlank(coordinates.artifactId())) {
            throw new IllegalArgumentException("Cannot resolve coordinates, artifactId is missing");
        }
        MavenCoordinates parentCoordinates = DomHelper.getParentPom(document())
                .map(ParentPom::validateCoordinates)
                .orElseThrow(
                        () -> new IllegalArgumentException("Cannot resolve coordinates, parent element is missing"));
        return new MavenCoordinates(
                StringUtils.defaultIfBlank(coordinates.groupId(), parentCoordinates.groupId()),
                coordinates.artifactId(),
                StringUtils.defaultIfBlank(coordinates.version(), parentCoordinates.version()));
    }

    default Input mapDocument(UnaryOperator<DocumentWrapper> mapper) {
        DocumentWrapper document = document();
        DocumentWrapper mapped = mapper.apply(document);
        return document == mapped ? this : new MergedInput(mapped, pomFile());
    }
}
