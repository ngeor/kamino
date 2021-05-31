package com.github.ngeor.yak4j;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link XmlUtil}.
 */
class XmlUtilTest {
    private XmlUtil xmlUtil;

    /**
     * Using a pom that does not have any elements.
     */
    @Nested
    class EmptyPom {
        private InputStream inputStream;

        @BeforeEach
        void beforeEach() throws ParserConfigurationException {
            inputStream = getClass().getResourceAsStream("/sample-pom.xml");
            xmlUtil = new XmlUtil();
        }

        @Test
        void noPath() throws IOException, SAXException {
            assertThatThrownBy(() -> xmlUtil.getElementContents(inputStream))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void testGroupId() throws IOException, SAXException {
            assertThat(xmlUtil.getElementContents(inputStream, "groupId")).containsExactly("com.github.ngeor");
        }

        @Test
        void testModules() throws IOException, SAXException {
            assertThat(xmlUtil.getElementContents(inputStream, "modules")).isEmpty();
        }

        @Test
        void testModule() throws IOException, SAXException {
            assertThat(xmlUtil.getElementContents(inputStream, "modules", "module")).isEmpty();
        }

        @Test
        void testSubModule() throws IOException, SAXException {
            assertThat(xmlUtil.getElementContents(inputStream, "modules", "module", "sub-module")).isEmpty();
        }
    }

    /**
     * Using a pom that has modules.
     */
    @Nested
    class ModulesPom {
        private InputStream inputStream;

        @BeforeEach
        void beforeEach() throws ParserConfigurationException {
            inputStream = getClass().getResourceAsStream("/modules-pom.xml");
            xmlUtil = new XmlUtil();
        }

        @Test
        void testModule() throws IOException, SAXException {
            assertThat(xmlUtil.getElementContents(inputStream, "modules", "module")).containsExactly("one", "two");
        }
    }
}
