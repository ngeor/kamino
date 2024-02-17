package com.github.ngeor.yak4j;

import static com.github.ngeor.yak4j.Util.loadSwaggerDocument;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DefinitionInliner}.
 */
class DefinitionInlinerTest {
    @Test
    void inline() throws IOException {
        // arrange
        DefinitionInliner inliner = new DefinitionInliner();
        SwaggerDocument document = loadSwaggerDocument("/address-book.yml");
        SwaggerDocument errors = loadSwaggerDocument("/error.yml");
        Map<String, SwaggerDocument> map = new HashMap<>();
        map.put("error.yml", errors);

        // act
        inliner.inline(document, map);

        // assert
        assertThat(document.lookupValue("paths./addresses.get.responses.401.schema.$ref"))
                .isEqualTo("#/definitions/ErrorInfo");
        assertThat(document.lookupValue("paths./addresses/{addressId}.delete.responses.404.schema.$ref"))
                .isEqualTo("#/definitions/ErrorInfo");
        SwaggerDocumentFragment definitions = document.getFragment("definitions");
        SwaggerDocumentFragment errorInfo = definitions.getFragment("ErrorInfo");
        assertThat(errorInfo).isNotNull();
        assertThat(errorInfo.getValue("description")).isEqualTo("An error thrown by the API");
        SwaggerDocumentFragment errorDetail = definitions.getFragment("ErrorDetail");
        assertThat(errorDetail).isNotNull();
        assertThat(errorDetail.getValue("description"))
                .isEqualTo("Details about an error, usually specific to a field.");
    }
}
