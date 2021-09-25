package com.github.ngeor.yak4jdom;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A wrapper for a DOM Element.
 */
public class ElementWrapper {
    private final Element element;

    public ElementWrapper(Element element) {
        this.element = element;
    }

    public String getTextContent() {
        return this.element.getTextContent();
    }

    public Iterator<Node> getChildNodesAsIterator() {
        return new ChildNodesIterator(this.element);
    }

    public Stream<Node> getChildNodesAsStream() {
        Iterable<Node> iterable = this::getChildNodesAsIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Gets the child elements of the given name.
     */
    public Stream<ElementWrapper> findChildElements(String childElementName) {
        return getChildNodesAsStream()
            .filter(node -> node.getNodeType() == Node.ELEMENT_NODE && childElementName.equals(node.getNodeName()))
            .map(node -> new ElementWrapper((Element) node));
    }

    public Optional<ElementWrapper> firstElement(String childElementName) {
        return findChildElements(childElementName).findFirst();
    }

    public String firstElementText(String childElementName) {
        return firstElement(childElementName).map(ElementWrapper::getTextContent).orElse(null);
    }

    public void setTextContent(String textContent) {
        this.element.setTextContent(textContent);
    }

    public void appendChild(ElementWrapper childElement) {
        this.element.appendChild(childElement.element);
    }

    /**
     * Removes all child nodes of this element.
     */
    public void removeChildNodes() {
        NodeList childNodes = this.element.getChildNodes();
        for (int i = childNodes.getLength() - 1; i >= 0; i--) {
            this.element.removeChild(childNodes.item(i));
        }
    }
}
