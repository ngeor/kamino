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

    public Optional<String> getTextContentOptional() {
        return Optional.ofNullable(getTextContent()).map(String::trim).filter(s -> !s.isEmpty());
    }

    public Iterator<Node> getChildNodesAsIterator() {
        return new ChildNodesIterator(this.element);
    }

    public Stream<Node> getChildNodesAsStream() {
        Iterable<Node> iterable = this::getChildNodesAsIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public Stream<ElementWrapper> getChildElements() {
        Iterable<ElementWrapper> iterable = this::getChildElementsAsIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public Iterator<ElementWrapper> getChildElementsAsIterator() {
        return new ChildElementWrapperIterator(element);
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
        return firstElement(childElementName).orElseGet(() -> append(childElementName));
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

    public ElementWrapper appendText(String text) {
        appendChild(element.getOwnerDocument().createTextNode(text));
        return this;
    }

    public Node importNode(Node node, boolean deep) {
        return element.getOwnerDocument().importNode(node, deep);
    }

    public void appendChild(Node newChild) {
        element.appendChild(newChild);
    }

    public void indent() {
        indent(1);
    }

    public boolean hasChildElements() {
        return getChildElements().findAny().isPresent();
    }

    private void indent(int level) {
        if (!hasChildElements()) {
            return;
        }

        Node node = element.getFirstChild();
        boolean isFirst = true;
        while (node != null) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                if (node.getNodeValue() == null || node.getNodeValue().trim().isEmpty()) {
                    Node temp = node.getNextSibling();
                    element.removeChild(node);
                    node = temp;
                } else {
                    // found text with content?
                    node = node.getNextSibling();
                }
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                Node temp = node.getNextSibling();

                if (isFirst) {
                    element.insertBefore(element.getOwnerDocument().createTextNode("\n"), node);
                    isFirst = false;
                }

                element.insertBefore(indentationNode(level), node);

                new ElementWrapper((Element) node).indent(level + 1);

                element.insertBefore(element.getOwnerDocument().createTextNode("\n"), temp);

                node = temp;
            } else {
                node = node.getNextSibling();
            }
        }
        element.appendChild(indentationNode(level - 1));
    }

    private Node indentationNode(int level) {
        String text = "";
        for (int i = 1; i <= level; i++) {
            text += "    ";
        }
        return element.getOwnerDocument().createTextNode(text);
    }

    public Optional<ElementWrapper> appendIfMissing(String childElementName) {
        if (firstElement(childElementName).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(append(childElementName));
    }

    public ElementWrapper append(String childElementName) {
        Element newChild = element.getOwnerDocument().createElement(childElementName);
        element.appendChild(newChild);
        return new ElementWrapper(newChild);
    }
}
