package com.github.ngeor.yak4j;

import static com.github.ngeor.yak4j.Util.loadSwaggerDocument;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SwaggerParser}.
 */
@SuppressWarnings("unchecked")
class SwaggerParserTest {
    private SwaggerDocument result;

    /**
     * Unit tests with the address-book.yml test data.
     */
    @Nested
    class AddressBook {
        @BeforeEach
        void beforeEach() throws IOException {
            // act
            // the imported static method uses SwaggerParser
            result = loadSwaggerDocument("/address-book.yml");
        }

        @Test
        void resultIsNotEmpty() {
            assertThat(result.size()).isGreaterThan(0);
        }

        @Test
        void swaggerField() {
            assertThat(result.getValue("swagger")).isEqualTo("2.0");
        }

        @Test
        void hostField() {
            assertThat(result.getValue("host")).isEqualTo("address-book.acme.com");
        }

        @Test
        void infoTitle() {
            SwaggerDocumentFragment info = result.getFragment("info");
            assertThat(info.getValue("title")).isEqualTo("Address Book Service");
        }

        @Test
        void infoLicenseName() {
            SwaggerDocumentFragment info = result.getFragment("info");
            SwaggerDocumentFragment license = info.getFragment("license");
            assertThat(license.getValue("name")).isEqualTo("Private License");
        }

        @Test
        void schemes() {
            List<String> schemes = result.getValues("schemes");
            assertThat(schemes).containsExactly("https");
        }

        @Test
        void security() {
            List<SwaggerDocumentFragment> security = result.getFragments("security");
            assertThat(security).hasSize(1);
            SwaggerDocumentFragment securityFragment = security.get(0);
            List<String> jwtAuthContents = securityFragment.getValues("jwtAuth");
            assertThat(jwtAuthContents).isEmpty();
        }

        @Test
        void paths() {
            SwaggerDocumentFragment paths = result.getFragment("paths");
            assertThat(paths.size()).isGreaterThan(0);
        }
    }

    /**
     * Unit tests with the pricing.yml test data.
     */
    @Nested
    class Pricing {
        @BeforeEach
        void beforeEach() throws IOException {
            // act
            // the imported static method uses SwaggerParser
            result = loadSwaggerDocument("/pricing.yml");
        }

        @Test
        void resultIsNotEmpty() {
            assertThat(result.size()).isGreaterThan(0);
        }

        @Test
        @SuppressWarnings("checkstyle:MagicNumber")
        void supportsFloatNumber() {
            SwaggerDocumentFragment weightFragment = result.lookupFragment("definitions.Parcel.properties.weight");
            Object minimum = weightFragment.get("minimum");
            assertThat(minimum).isInstanceOf(Double.class);
            assertThat((double) minimum).isCloseTo(0.1, Percentage.withPercentage(0.001));
        }
    }
}
