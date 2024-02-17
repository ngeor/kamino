package com.github.ngeor.yak4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link XmlSerializer}.
 */
class XmlSerializerTest {
    private XmlSerializer xmlSerializer;

    @BeforeEach
    void beforeEach() {
        xmlSerializer = new XmlSerializer();
    }

    /**
     * Unit tests for {@link XmlSerializer#serialize}.
     */
    @Nested
    class Serialize {
        @Test
        void canSerializeInfo() {
            Info info = new Info();
            info.setName("Nikolaos");
            String result = xmlSerializer.serialize(info, Info.class);
            assertThat(result)
                    .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<info><name>Nikolaos</name></info>");
        }

        /**
         * This test just proves that JAXBException is wrapped inside
         * XmlRuntimeException.
         */
        @Test
        void cannotSerializeThisTest() {
            assertThatThrownBy(() -> xmlSerializer.serialize(this, Serialize.class))
                    .isInstanceOf(XmlRuntimeException.class)
                    .hasCauseInstanceOf(JAXBException.class);
        }
    }

    /**
     * Unit tests for {@link XmlSerializer#deserialize}.
     */
    @Nested
    class Deserialize {
        @Test
        void canDeserializeInfo() {
            String input = "<info><name>Hello, world</name></info>";
            Info result = xmlSerializer.deserialize(input, Info.class);
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Hello, world");
        }

        @Test
        void cannotDeserializeInvalidXml() {
            assertThatThrownBy(() -> xmlSerializer.deserialize("oops", Info.class))
                    .isInstanceOf(XmlRuntimeException.class)
                    .hasCauseInstanceOf(JAXBException.class);
        }
    }

    /**
     * Unit tests for {@link XmlSerializer#deserializeAny}.
     */
    @Nested
    class DeserializeAny {
        @Test
        void canDeserializeInfo() {
            String input = "<info><name>Hello, world</name></info>";
            Object result = xmlSerializer.deserializeAny(input, Info.class);
            assertThat(result).isNotNull().isInstanceOf(Info.class);
            assertThat(((Info) result).getName()).isEqualTo("Hello, world");
        }

        @Test
        void canDeserializeInfo2() {
            String input = "<info2><name>Hello, world</name></info2>";
            Object result = xmlSerializer.deserializeAny(input, Info.class, Info2.class);
            assertThat(result).isNotNull().isInstanceOf(Info2.class);
            assertThat(((Info2) result).getName()).isEqualTo("Hello, world");
        }

        @Test
        void cannotDeserializeInvalidXml() {
            assertThatThrownBy(() -> xmlSerializer.deserializeAny("oops", Info.class, Info2.class))
                    .isInstanceOf(XmlRuntimeException.class)
                    .hasCauseInstanceOf(JAXBException.class);
        }
    }
}
