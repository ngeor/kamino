package com.github.ngeor.yak4j;

/**
 * Prefixes definitions with a prefix.
 * This includes the usages of the definition in other definitions and in operations.
 */
public class DefinitionPrefixer {
    /**
     * Prefixes model definitions with the given prefix.
     * @param swaggerDocument The swagger document.
     * @param prefix The model prefix.
     */
    public void prefix(SwaggerDocument swaggerDocument, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return;
        }

        SwaggerDocumentFragment definitions = swaggerDocument.getFragment("definitions");
        String[] keys = definitions.keys();
        for (String key : keys) {
            definitions.renameKey(key, prefix + key);
        }

        swaggerDocument.visit((key, value) -> {
            if (key.equals("$ref") && ((String) value).startsWith("#/definitions/")) {
                return "#/definitions/" + prefix + ((String) value).substring("#/definitions/".length());
            }

            return value;
        });
    }
}
