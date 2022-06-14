package com.github.ngeor.yak4j;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.ngeor.yak4j.Util.loadSwaggerDocument;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PathPrefixer}.
 */
class PathPrefixerTest {
    @Test
    void test() throws IOException {
        // arrange
        SwaggerDocument swaggerDocument = loadSwaggerDocument("/address-book.yml");
        PathPrefixer pathPrefixer = new PathPrefixer();

        // act
        pathPrefixer.prefix(swaggerDocument, "/address-book");

        // assert
        SwaggerDocumentFragment paths = swaggerDocument.getFragment("paths");
        assertThat(paths.keys()).containsExactly("/address-book/addresses", "/address-book/addresses/{addressId}");
    }
}
