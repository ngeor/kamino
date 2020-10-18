package com.github.ngeor.yak4j;

/**
 * Prefixes all Swagger paths with a prefix.
 */
public class PathPrefixer {
    /**
     * Prefixes paths with a prefix.
     * @param swaggerDocument The swagger document.
     * @param prefix The path prefix.
     */
    public void prefix(SwaggerDocument swaggerDocument, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return;
        }

        SwaggerDocumentFragment paths = swaggerDocument.getFragment("paths");
        String[] keys = paths.keys();
        for (String key : keys) {
            paths.renameKey(key, prefix + key);
        }
    }
}
