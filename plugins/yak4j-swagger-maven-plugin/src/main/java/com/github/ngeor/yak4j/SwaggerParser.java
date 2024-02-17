package com.github.ngeor.yak4j;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses a swagger YAML file.
 */
public class SwaggerParser {
    private YAMLParser parser;
    private JsonToken token;

    /**
     * Creates an instance of this class.
     *
     * @param inputStream The input stream.
     * @return A swagger document.
     * @throws IOException If an IO error occurs.
     */
    public SwaggerDocument parse(InputStream inputStream) throws IOException {
        YAMLFactory yamlFactory = new YAMLFactory();
        parser = yamlFactory.createParser(inputStream);

        readToken();
        verify(JsonToken.START_OBJECT);
        return new SwaggerDocument(readCurrentObject());
    }

    private Object readCurrentToken() throws IOException {
        if (token == JsonToken.START_OBJECT) {
            return readCurrentObject();
        } else if (token == JsonToken.START_ARRAY) {
            return readCurrentArray();
        } else if (token == JsonToken.VALUE_STRING) {
            return readCurrentString();
        } else if (token == JsonToken.VALUE_FALSE) {
            readToken();
            return false;
        } else if (token == JsonToken.VALUE_TRUE) {
            readToken();
            return true;
        } else if (token == JsonToken.VALUE_NUMBER_INT) {
            int result = parser.getValueAsInt();
            readToken();
            return result;
        } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            double result = parser.getValueAsDouble();
            readToken();
            return result;
        } else {
            throw new IllegalStateException("Unexpected token " + token);
        }
    }

    private List<Object> readCurrentArray() throws IOException {
        verify(JsonToken.START_ARRAY);
        readToken();

        List<Object> list = new ArrayList<>();

        while (token != JsonToken.END_ARRAY) {
            list.add(readCurrentToken());
        }

        verify(JsonToken.END_ARRAY);
        readToken();

        return list;
    }

    private String readCurrentString() throws IOException {
        verify(JsonToken.VALUE_STRING);
        String result = parser.getValueAsString();
        readToken();
        return result;
    }

    private Map<String, Object> readCurrentObject() throws IOException {
        // read past START_OBJECT
        verify(JsonToken.START_OBJECT);
        readToken();

        Map<String, Object> result = new LinkedHashMap<>();
        while (token != JsonToken.END_OBJECT) {
            verify(JsonToken.FIELD_NAME);
            String fieldName = parser.getValueAsString();
            readToken();

            result.put(fieldName, readCurrentToken());
        }

        verify(JsonToken.END_OBJECT);
        readToken();

        return result;
    }

    private void readToken() throws IOException {
        token = parser.nextToken();
    }

    private void verify(JsonToken wantedToken) {
        if (token != wantedToken) {
            throw new IllegalStateException(String.format("Expected %s, found %s", wantedToken, token));
        }
    }
}
