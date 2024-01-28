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
        map.forEach((key, value) -> {
            try {
                writeFieldName(generator, key);
                writeObject(generator, value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        if (value instanceof SwaggerDocumentFragment) {
            writeMap(generator, (SwaggerDocumentFragment) value);
        } else if (value instanceof List) {
            writeList(generator, (List) value);
        } else if (value instanceof Boolean) {
            generator.writeBoolean((Boolean) value);
        } else if (value instanceof Integer) {
            generator.writeNumber((Integer) value);
        } else if (value instanceof Double) {
            generator.writeNumber((double) value);
        } else {
            generator.writeString(value.toString());
        }
    }

    private void writeList(YAMLGenerator generator, List list) throws IOException {
        generator.writeStartArray();
        for (Object value : list) {
            writeObject(generator, value);
        }

        generator.writeEndArray();
    }
}
