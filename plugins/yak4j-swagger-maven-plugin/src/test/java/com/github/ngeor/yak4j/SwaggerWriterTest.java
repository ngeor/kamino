package com.github.ngeor.yak4j;

import static com.github.ngeor.yak4j.Util.loadSwaggerDocument;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link SwaggerWriter}.
 */
class SwaggerWriterTest {
    @ParameterizedTest
    @ValueSource(strings = {"/address-book.yml", "/auth.yml", "/pricing.yml"})
    void test(String resourceName) throws IOException {
        // arrange
        SwaggerDocument swaggerDocument = loadSwaggerDocument(resourceName);
        SwaggerWriter writer = new SwaggerWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // act
        writer.write(swaggerDocument, byteArrayOutputStream);

        // assert
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        SwaggerParser parser = new SwaggerParser();
        SwaggerDocument newDocument = parser.parse(byteArrayInputStream);
        assertThat(newDocument).isEqualTo(swaggerDocument);
    }
}
