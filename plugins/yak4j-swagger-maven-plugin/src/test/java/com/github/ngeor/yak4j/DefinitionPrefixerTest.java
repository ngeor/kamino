package com.github.ngeor.yak4j;

import static com.github.ngeor.yak4j.Util.loadSwaggerDocument;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DefinitionPrefixer}.
 */
class DefinitionPrefixerTest {
    private SwaggerDocument swaggerDocument;

    /**
     * Unit tests for the AddressBook test data.
     */
    @Nested
    class AddressBook {
        @BeforeEach
        void beforeEach() throws IOException {
            // arrange
            swaggerDocument = loadSwaggerDocument("/address-book.yml");
            DefinitionPrefixer definitionPrefixer = new DefinitionPrefixer();

            // act
            definitionPrefixer.prefix(swaggerDocument, "AddressBook");
        }

        @Test
        void definitions() {
            SwaggerDocumentFragment definitions = swaggerDocument.getFragment("definitions");
            assertThat(definitions.keys()).containsExactly("AddressBookAddress", "AddressBookContactInfo");
        }

        @Test
        void definitionReference() {
            String ref = swaggerDocument.lookupValue("definitions.AddressBookAddress.properties.contactInfo.$ref");
            assertThat(ref).isEqualTo("#/definitions/AddressBookContactInfo");
        }
    }

    /**
     * Unit tests for the Pricing test data.
     */
    @Nested
    class Pricing {
        @BeforeEach
        void beforeEach() throws IOException {
            // arrange
            swaggerDocument = loadSwaggerDocument("/pricing.yml");
            DefinitionPrefixer definitionPrefixer = new DefinitionPrefixer();

            // act
            definitionPrefixer.prefix(swaggerDocument, "Pricing");
        }

        @Test
        void insideArray() {
            SwaggerDocumentFragment definitions = swaggerDocument.getFragment("definitions");
            SwaggerDocumentFragment pricingShipment = definitions.getFragment("PricingShipment");
            List<SwaggerDocumentFragment> allOf = pricingShipment.getFragments("allOf");
            SwaggerDocumentFragment firstType = allOf.get(0);
            String ref = firstType.getValue("$ref");
            assertThat(ref).isEqualTo("#/definitions/PricingShipmentWithoutProduct");
        }
    }
}
