package com.github.ngeor.xml;

import java.util.Objects;

public final class XmlEvent {
    private final String text;
    private final XmlEventType eventType;

    public XmlEvent(String text, XmlEventType eventType) {
        this.text = text;
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlEvent other = (XmlEvent) o;
        return Objects.equals(text, other.text) && eventType == other.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, eventType);
    }

    @Override
    public String toString() {
        return text + " " + eventType;
    }

    public String getText() {
        return text;
    }

    public XmlEventType getEventType() {
        return eventType;
    }

    public boolean isEndElement() {
        return eventType == XmlEventType.END_ELEMENT;
    }

    public boolean isStartElement() {
        return eventType == XmlEventType.BEGIN_ELEMENT;
    }

    public boolean isCharacters() {
        return eventType == XmlEventType.TEXT;
    }

    public String getNodeName() {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length() && !Character.isLetter(text.charAt(i))) {
            i++;
        }
        while (i < text.length() && Character.isLetterOrDigit(text.charAt(i))) {
            result.append(text.charAt(i));
            i++;
        }

        return result.toString();
    }
}
