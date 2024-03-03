package com.github.ngeor.maven.document.effective;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.parent.ParentLoader;
import java.util.Objects;

public class DefaultParentResolver implements ParentResolver {
    private final ParentLoader parentLoader;

    public DefaultParentResolver(ParentLoader parentLoader) {
        this.parentLoader = Objects.requireNonNull(parentLoader);
    }

    @Override
    public DocumentLoader resolveWithParentRecursively(DocumentLoader input) {
        DocumentLoader optionalParentInput = parentLoader.loadParent(input).orElse(null);
        if (optionalParentInput == null) {
            return input;
        }

        DocumentLoader resolvedParent = resolveWithParentRecursively(optionalParentInput);
        return merge(resolvedParent, input);
    }

    private DocumentLoader merge(DocumentLoader parent, DocumentLoader child) {
        return parent.mapDocument(parentDoc -> PomMerger.mergeIntoLeft(parentDoc.deepClone(), child.loadDocument()));
    }
}
