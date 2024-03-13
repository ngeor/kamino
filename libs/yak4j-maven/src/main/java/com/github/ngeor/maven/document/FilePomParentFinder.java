package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ParentPom;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;

public class FilePomParentFinder implements ParentFinderNg {
    @Override
    public Optional<PomDocument> findParent(PomDocument pomDocument) {
        return pomDocument.parentPom().map(p -> map(pomDocument, p));
    }

    private PomDocument map(PomDocument pomDocument, ParentPom parentPom) {
        // try through relativePath (unless explicitly set to empty)
        String relativePath = Objects.requireNonNullElse(parentPom.relativePath(), "../pom.xml");
        if (!relativePath.isBlank()) {
            if (pomDocument instanceof FilePomDocument f) {
                File parentFile = f.getPomFile().toPath().resolve(relativePath).toFile();
                return new FilePomDocument(parentFile);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        // try through local repository
        throw new NotImplementedException();
    }
}
