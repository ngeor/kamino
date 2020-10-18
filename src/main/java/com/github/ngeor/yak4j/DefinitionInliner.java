package com.github.ngeor.yak4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Consolidates external definitions into a Swagger document.
 * <p>
 * Example:
 * <pre>
 * schema:
 *   $ref: './error.yml#/ErrorInfo' becomes '#/definitions/ErrorInfo'
 * </pre>
 */
public class DefinitionInliner {
    private final Set<String> alreadyAdded = new HashSet<>();

    /**
     * Consolidates external definitions into this Swagger document.
     *
     * @param document The document.
     * @param inlineDocs Inline definition documents.
     */
    public void inline(SwaggerDocument document, Map<String, SwaggerDocument> inlineDocs) {
        SwaggerDocumentFragment definitions = document.getFragment("definitions");
        document.visit((key, value) -> {
            if (!"$ref".equals(key)) {
                return value;
            }

            if (!(value instanceof String)) {
                return value;
            }

            String ref = (String) value;
            if (ref.startsWith("#/definitions/")) {
                return value;
            }

            String[] parts = ref.split("#");
            if (parts.length != 2) {
                return value;
            }

            String filename = getFilename(parts[0]);
            SwaggerDocumentFragment inlineDefinition = inlineDocs.get(filename);
            if (inlineDefinition == null) {
                return value;
            }

            if (!alreadyAdded.contains(filename)) {
                alreadyAdded.add(filename);
                definitions.append(inlineDefinition);
            }

            String definition = parts[1];
            return "#/definitions" + definition;
        });
    }

    private String getFilename(String path) {
        String[] parts = path.split("[\\\\/]");
        return parts[parts.length - 1];
    }
}
