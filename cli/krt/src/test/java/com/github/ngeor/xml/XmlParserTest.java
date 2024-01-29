package com.github.ngeor.xml;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("MagicNumber")
class XmlParserTest {
    @Test
    void declaration() throws IOException {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        XmlParser parser = new XmlParser(input);
        List<XmlEvent> result = parse(parser);
        assertThat(result).containsExactly(new XmlEvent(input, XmlEventType.DECLARATION));
    }

    @Test
    void element() throws IOException {
        String input = "<p>hello</p>";
        XmlParser parser = new XmlParser(input);
        List<XmlEvent> result = parse(parser);
        assertThat(result).containsExactly(
            new XmlEvent("<p>", XmlEventType.BEGIN_ELEMENT),
            new XmlEvent("hello", XmlEventType.TEXT),
            new XmlEvent("</p>", XmlEventType.END_ELEMENT)
        );
    }

    @Test
    void elementWithAttributes() throws IOException {
        String input = "<node color='blue' off></node>";
        XmlParser parser = new XmlParser(input);
        List<XmlEvent> result = parse(parser);
        assertThat(result).containsExactly(
            new XmlEvent("<node color='blue' off>", XmlEventType.BEGIN_ELEMENT),
            new XmlEvent("</node>", XmlEventType.END_ELEMENT)
        );
    }

    @Test
    void selfClosingElement() throws IOException {
        String input = "<node />";
        XmlParser parser = new XmlParser(input);
        List<XmlEvent> result = parse(parser);
        assertThat(result).containsExactly(
            new XmlEvent("<node />", XmlEventType.BEGIN_ELEMENT),
            new XmlEvent("", XmlEventType.END_ELEMENT)
        );
    }

    @Test
    void comment() throws IOException {
        String input = "<!-- <p> -->";
        XmlParser parser = new XmlParser(input);
        List<XmlEvent> result = parse(parser);
        assertThat(result).containsExactly(
            new XmlEvent(input, XmlEventType.COMMENT)
        );
    }

    private List<XmlEvent> parse(XmlParser parser) throws IOException {
        var iterator = parser.iterator();
        List<XmlEvent> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}
