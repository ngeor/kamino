package com.github.ngeor.maven.document;

import com.github.ngeor.maven.document.cache.CanonicalFile;
import com.github.ngeor.maven.dom.ParentPom;
import java.io.File;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.NotImplementedException;

public class FilePomParentFinder extends BaseParentFinderNg<CanonicalFile, FilePomDocument> {
    public FilePomParentFinder(UnaryOperator<FilePomDocument> documentDecorator) {
        super(documentDecorator);
    }

    @Override
    protected CanonicalFile cacheKey(FilePomDocument child) {
        return new CanonicalFile(child.getPomFile());
    }

    @Override
    protected FilePomDocument doFindParent(FilePomDocument pomDocument, ParentPom parentPom) {
        // try through relativePath (unless explicitly set to empty)
        String relativePath = Objects.requireNonNullElse(parentPom.relativePath(), "../pom.xml");
        if (!relativePath.isBlank()) {
            File parentFile = pomDocument.getPomFile().toPath().resolve(relativePath).toFile();
            return new FilePomDocument(parentFile);
        }
        // try through local repository
        throw new NotImplementedException();
    }
}
