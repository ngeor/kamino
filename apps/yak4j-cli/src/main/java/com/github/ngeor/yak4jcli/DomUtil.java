package com.github.ngeor.yak4jcli;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities for XML documents.
 */
public final class DomUtil {
    private DomUtil() {
    }

    /**
     * Gets the XML text content of a child node.
     */
    public static String getXmlTextContent(Element parentElement, String childElementName) {
        Element childElement = getChildElement(parentElement, childElementName).orElse(null);
        String textContent = childElement == null ? null : childElement.getTextContent();
        return textContent == null ? null : textContent.trim();
    }

    /**
     * Gets the child element of a parent element.
     */
    public static Optional<Element> getChildElement(Element parentElement, String childElementName) {
        if (parentElement == null) {
            return Optional.empty();
        }

        Iterator<Element> iterator = new ChildElementsIterator(parentElement, childElementName);
        return Optional.ofNullable(iterator.next());
    }

    /**
     * Gets the child elements of a parent element that match the given name.
     */
    public static Stream<Element> getChildElements(Element parentElement, String childElementName) {
        if (parentElement == null) {
            return Stream.empty();
        }

        Iterator<Element> iterator = new ChildElementsIterator(parentElement, childElementName);
        Iterable<Element> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Iterator of child elements.
     */
    static class ChildElementsIterator implements Iterator<Element> {
        private final Element parentElement;
        private final String childElementName;
        private int index;
        private NodeList childNodes;
        private Element foundNext;
        private boolean foundEof;

        /**
         * Creates an instance of this class.
         */
        ChildElementsIterator(Element parentElement, String childElementName) {
            this.parentElement = parentElement;
            this.childElementName = childElementName;
            this.index = 0;
            this.foundEof = false;
            this.foundNext = null;
        }

        @Override
        public boolean hasNext() {
            doNext();
            return this.foundNext != null;
        }

        @Override
        public Element next() {
            doNext();
            Element result = this.foundNext;
            this.foundNext = null;
            return result;
        }

        private void doNext() {
            if (foundEof || foundNext != null) {
                return;
            }
            if (this.index == 0) {
                childNodes = parentElement.getChildNodes();
            }
            while (index < childNodes.getLength() && this.foundNext == null) {
                Node item = childNodes.item(index);
                if (item.getNodeType() == Node.ELEMENT_NODE && childElementName.equals(item.getNodeName())) {
                    this.foundNext = (Element) item;
                }
                this.index++;
            }
            this.foundEof = this.index >= childNodes.getLength();
        }
    }
}
