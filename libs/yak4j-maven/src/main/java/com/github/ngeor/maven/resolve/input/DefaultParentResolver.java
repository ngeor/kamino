package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.maven.resolve.PomMerger;
import java.util.Objects;

public class DefaultParentResolver implements ParentResolver {
    private final ParentLoader parentLoader;

    public DefaultParentResolver(ParentLoader parentLoader) {
        this.parentLoader = Objects.requireNonNull(parentLoader);
    }

    @Override
    public Input resolveWithParentRecursively(Input input) {
        Input optionalParentInput = parentLoader.loadParent(input).orElse(null);
        if (optionalParentInput == null) {
            return input;
        }

        Input resolvedParent = resolveWithParentRecursively(optionalParentInput);
        return merge(resolvedParent, input);
    }

    private Input merge(Input parent, Input child) {
        return parent.mapDocument(parentDoc -> PomMerger.mergeIntoLeft(parentDoc.deepClone(), child.document()));
    }
}
