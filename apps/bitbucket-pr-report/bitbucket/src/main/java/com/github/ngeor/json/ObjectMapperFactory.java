package com.github.ngeor.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utility class that prepares the ObjectMapper.
 */
public final class ObjectMapperFactory {
    private ObjectMapperFactory() {
    }

    /**
     * Creates the ObjectMapper used in the application.
     *
     * @return A configured instance of {@link ObjectMapper}.
     */
    public static ObjectMapper create() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
