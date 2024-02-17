package com.github.ngeor.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("MagicNumber")
class XmlEventTest {
    @ParameterizedTest
    @CsvSource({"<p>, p", "<p color>, p", "<node/>, node", "<a42>, a42"})
    void getNodeName(String text, String expectedNodeName) {
        XmlEvent event = new XmlEvent(text, XmlEventType.BEGIN_ELEMENT);
        String nodeName = event.getNodeName();
        assertThat(nodeName).isEqualTo(expectedNodeName);
    }
}
