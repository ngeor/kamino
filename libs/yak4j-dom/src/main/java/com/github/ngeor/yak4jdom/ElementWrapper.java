package com.github.ngeor.yak4jdom;

import java.util.Iterator;
import java.util.Objects;
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

    public String getNodeName() {
        return this.element.getNodeName();
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

    public Stream<ElementWrapper> getChildElements() {
        return getChildNodesAsStream()
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element)
                .map(node -> new ElementWrapper((Element) node));
    }

    /**
     * Gets the child elements of the given name.
     */
    public Stream<ElementWrapper> findChildElements(String childElementName) {
        return getChildElements().filter(element -> Objects.equals(element.getNodeName(), childElementName));
    }

    public Optional<ElementWrapper> firstElement(String childElementName) {
        return findChildElements(childElementName).findFirst();
    }

    public String firstElementText(String childElementName) {
        return firstElement(childElementName)
                .map(ElementWrapper::getTextContent)
                .orElse(null);
    }

    public ElementWrapper ensureChild(String childElementName) {
        return firstElement(childElementName).orElseGet(() -> {
            Element newChild = element.getOwnerDocument().createElement(childElementName);
            element.appendChild(newChild);
            return new ElementWrapper(newChild);
        });
    }

    public boolean ensureChildText(String childElementName, String text) {
        ElementWrapper child = ensureChild(childElementName);
        if (text.equals(child.getTextContent())) {
            return false;
        }
        child.setTextContent(text);
        return true;
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
    public void removeAllChildNodes() {
        NodeList childNodes = this.element.getChildNodes();
        for (int i = childNodes.getLength() - 1; i >= 0; i--) {
            this.element.removeChild(childNodes.item(i));
        }
    }

    public void removeChildNodesByName(String name) {
        NodeList childNodes = this.element.getChildNodes();
        for (int i = childNodes.getLength() - 1; i >= 0; i--) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE && name.equals(item.getNodeName())) {
                this.element.removeChild(item);
            }
        }
    }
}
