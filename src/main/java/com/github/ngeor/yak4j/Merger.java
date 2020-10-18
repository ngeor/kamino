package com.github.ngeor.yak4j;

/**
 * Merges swagger documents.
 */
public class Merger {
    /**
     * Merges swagger documents.
     * @param first The first document.
     * @param second The second document.
     */
    public void merge(SwaggerDocument first, SwaggerDocument second) {
        // TODO move security inside operations (no top level)

        // merge paths
        first.ensureFragment("paths").append(second.getFragment("paths"));

        // merge definitions
        first.ensureFragment("definitions").append(second.getFragment("definitions"));
    }
}
