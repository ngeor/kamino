package com.github.ngeor.yak4j;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Writes a Swagger document as YAML.
 */
public class SwaggerWriter {
    /**
     * Writes the given document to the specified stream.
     *
     * @param swaggerDocument The document.
     * @param outputStream    The stream.
     * @throws IOException An IO error.
     */
    public void write(SwaggerDocumentFragment swaggerDocument, OutputStream outputStream) throws IOException {
        YAMLFactory yamlFactory = new YAMLFactory().enable(Feature.USE_PLATFORM_LINE_BREAKS);
        YAMLGenerator generator = yamlFactory.createGenerator(outputStream);
        writeMap(generator, swaggerDocument);
        generator.close();
    }

    private void writeMap(YAMLGenerator generator, SwaggerDocumentFragment map) throws IOException {
        generator.writeStartObject();
        for (String key : map.keys()) {
            writeFieldName(generator, key);
            writeObject(generator, map.get(key));
        }
        generator.writeEndObject();
    }

    private void writeFieldName(YAMLGenerator generator, String key) throws IOException {
        try {
            int keyAsInt = Integer.parseUnsignedInt(key);
            generator.writeFieldId(keyAsInt);
        } catch (NumberFormatException ignored) {
            generator.writeFieldName(key);
        }
    }

    private void writeObject(YAMLGenerator generator, Object value) throws IOException {
        if (value instanceof SwaggerDocumentFragment fragment) {
            writeMap(generator, fragment);
        } else if (value instanceof List<?> list) {
            writeList(generator, list);
        } else if (value instanceof Boolean boolean1) {
            generator.writeBoolean(boolean1);
        } else if (value instanceof Integer integer) {
            generator.writeNumber(integer);
        } else if (value instanceof Double double1) {
            generator.writeNumber(double1);
        } else {
            generator.writeString(value.toString());
        }
    }

    private void writeList(YAMLGenerator generator, List<?> list) throws IOException {
        generator.writeStartArray();
        for (Object value : list) {
            writeObject(generator, value);
        }

        generator.writeEndArray();
    }
}
