package com.github.ngeor.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;

public class XmlParser {
    private final PushbackReader reader;

    public XmlParser(PushbackReader reader) {
        this.reader = reader;
    }

    public XmlParser(String input) {
        this(new PushbackReader(new StringReader(input)));
    }

    public XmlParser(InputStream inputStream) {
        this(new PushbackReader(new InputStreamReader(inputStream)));
    }

    public Iterator iterator() {
        return new Iterator();
    }

    private List<XmlEvent> nextElements() throws IOException {
        List<XmlEvent> result = new ArrayList<>();
        String text = readTag();
        if (text.isEmpty()) {
            text = readUntilTag();
            if (!text.isEmpty()) {
                result.add(new XmlEvent(text, XmlEventType.TEXT));
            }
        } else {
            if (text.startsWith("<!--")) {
                result.add(new XmlEvent(text, XmlEventType.COMMENT));
            } else if (text.startsWith("<?")) {
                result.add(new XmlEvent(text, XmlEventType.DECLARATION));
            } else if (text.startsWith("</")) {
                result.add(new XmlEvent(text, XmlEventType.END_ELEMENT));
            } else {
                result.add(new XmlEvent(text, XmlEventType.BEGIN_ELEMENT));
                if (text.endsWith("/>")) {
                    result.add(new XmlEvent("", XmlEventType.END_ELEMENT));
                }
            }
        }
        return result;
    }

    private String readUntilTag() throws IOException {
        StringBuilder buffer = new StringBuilder();
        Optional<Character> next;
        do {
            next = readIf(ch -> ch != '<');
            next.ifPresent(buffer::append);
        } while (next.isPresent());
        return buffer.toString();
    }

    private String readTag() throws IOException {
        Optional<Character> next = readIf(ch -> ch == '<');
        if (next.isEmpty()) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(next.get());
        do {
            next = read();
            next.ifPresent(buffer::append);
            if (bufferStartsWith(buffer, "<!--")) {
                readComment(buffer);
            }
        } while (next.isPresent() && !bufferEndsWith(buffer, ">"));
        return buffer.toString();
    }

    private void readComment(StringBuilder buffer) throws IOException {
        while (!bufferEndsWith(buffer, "-->")) {
            Optional<Character> next = read();
            if (next.isPresent()) {
                buffer.append(next.get());
            } else {
                break;
            }
        }
    }

    private boolean bufferStartsWith(StringBuilder buffer, String needle) {
        int i = 0;
        int j = 0;
        while (i < needle.length() && j < buffer.length() && buffer.charAt(j) == needle.charAt(i)) {
            i++;
            j++;
        }
        return i == needle.length();
    }

    private boolean bufferEndsWith(StringBuilder buffer, String needle) {
        int i = needle.length() - 1;
        int j = buffer.length() - 1;
        while (i >= 0 && j >= 0 && buffer.charAt(j) == needle.charAt(i)) {
            i--;
            j--;
        }
        return i < 0;
    }

    private Optional<Character> read() throws IOException {
        int next = reader.read();
        return next != -1 ? Optional.of((char) next) : Optional.empty();
    }

    private Optional<Character> readIf(Predicate<Character> predicate) throws IOException {
        int next = reader.read();
        if (next != -1) {
            char ch = (char) next;
            if (predicate.test(ch)) {
                return Optional.of(ch);
            } else {
                reader.unread(next);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public class Iterator {
        private final Queue<XmlEvent> buffer = new LinkedList<>();

        public boolean hasNext() throws IOException {
            callNextIfNeeded();
            return !buffer.isEmpty();
        }

        public XmlEvent next() throws IOException {
            callNextIfNeeded();
            return buffer.remove();
        }

        private void callNextIfNeeded() throws IOException {
            if (!buffer.isEmpty()) {
                return;
            }
            List<XmlEvent> next = nextElements();
            buffer.addAll(next);
        }
    }
}
