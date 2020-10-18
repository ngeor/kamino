package com.github.ngeor.yak4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities for the unit tests.
 */
final class Util {
    private Util() {}

    /**
     * Loads a Swagger document from the resources.
     * @param resourceName The resource name.
     * @return The swagger document.
     * @throws IOException If an IO error occurs.
     */
    static SwaggerDocument loadSwaggerDocument(String resourceName) throws IOException {
        InputStream resourceAsStream = Util.class.getResourceAsStream(resourceName);
        if (resourceAsStream == null) {
            throw new IOException("Could not load resource " + resourceName);
        }

        SwaggerParser parser = new SwaggerParser();
        return parser.parse(resourceAsStream);
    }
}
