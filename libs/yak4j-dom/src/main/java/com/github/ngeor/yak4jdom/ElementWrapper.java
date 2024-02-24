package com.github.ngeor.yak4jdom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A wrapper for a DOM Element.
 */
public class ElementWrapper {
    private final Element element;

    public ElementWrapper(Element element) {
        this.element = Objects.requireNonNull(element);
    }

    public String getNodeName() {
        return this.element.getNodeName();
    }

    @Deprecated
    public Iterator<Node> getChildNodesAsIterator() {
        return new ChildNodesIterator(this.element);
    }

    public Iterator<ElementWrapper> getChildElementsAsIterator() {
        return new ChildElementWrapperIterator(element);
    }

    public Stream<ElementWrapper> getChildElements() {
        Iterable<ElementWrapper> iterable = this::getChildElementsAsIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
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

    public ElementWrapper ensureChild(String childElementName) {
        return firstElement(childElementName).orElseGet(() -> append(childElementName));
    }

    public ElementWrapper append(String childElementName) {
        Element newChild = element.getOwnerDocument().createElement(childElementName);
        element.appendChild(newChild);
        return new ElementWrapper(newChild);
    }

    public String getTextContent() {
        return this.element.getTextContent();
    }

    public void setTextContent(String textContent) {
        this.element.setTextContent(textContent);
    }

    public Optional<String> getTextContentTrimmed() {
        return Optional.ofNullable(getTextContent()).map(String::trim).filter(StringUtils::isNotEmpty);
    }

    public Stream<String> getTextContentTrimmedAsStream() {
        return getTextContentTrimmed().stream();
    }

    public Stream<String> childTextContentsTrimmed(String childElementName) {
        return findChildElements(childElementName).flatMap(ElementWrapper::getTextContentTrimmedAsStream);
    }

    public String firstElementText(String... childElementNames) {
        Validate.notEmpty(childElementNames);
        ElementWrapper e = this;
        for (int i = 0; i < childElementNames.length - 1 && e != null; i++) {
            e = e.firstElement(childElementNames[i]).orElse(null);
        }
        return Optional.ofNullable(e).stream()
                .flatMap(x -> x.childTextContentsTrimmed(childElementNames[childElementNames.length - 1]))
                .findFirst()
                .orElse(null);
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

    public void indent() {
        indent(1);
    }

    public boolean hasChildElements() {
        return getChildElements().findAny().isPresent();
    }

    private void indent(int level) {
        if (!hasChildElements()) {
            trimLeft();
            trimRight();
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
        Validate.isTrue(level >= 0);
        String oneLevelIndentation = "    ";
        String textContents = oneLevelIndentation.repeat(level);
        return element.getOwnerDocument().createTextNode(textContents);
    }

    public void trimLeft() {
        Node node = element.getFirstChild();
        while (node != null && node.getNodeType() == Node.TEXT_NODE) {
            String text = node.getNodeValue();
            if (text == null) {
                // remove node with null text
                Node temp = node.getNextSibling();
                element.removeChild(node);
                node = temp;
            } else {
                // trim left
                text = text.stripLeading();
                if (text.isEmpty()) {
                    // remove node with empty text
                    Node temp = node.getNextSibling();
                    element.removeChild(node);
                    node = temp;
                } else {
                    // replace text with trimmed value and stop loop
                    node.setNodeValue(text);
                    node = null;
                }
            }
        }
    }

    public void trimRight() {
        Node node = element.getLastChild();
        while (node != null && node.getNodeType() == Node.TEXT_NODE) {
            String text = node.getNodeValue();
            if (text == null) {
                // remove node with null text
                Node temp = node.getPreviousSibling();
                element.removeChild(node);
                node = temp;
            } else {
                // trim left
                text = text.stripLeading();
                if (text.isEmpty()) {
                    // remove node with empty text
                    Node temp = node.getPreviousSibling();
                    element.removeChild(node);
                    node = temp;
                } else {
                    // replace text with trimmed value and stop loop
                    node.setNodeValue(text);
                    node = null;
                }
            }
        }
    }

    public String path() {
        Node parentNode = element.getParentNode();
        if (parentNode == null
                || parentNode.getNodeType() == Node.DOCUMENT_NODE
                || (!(parentNode instanceof Element))
                || parentNode == element) {
            return getNodeName();
        }

        return new ElementWrapper((Element) parentNode).path() + "/" + getNodeName();
    }

    public Map<String, ElementWrapper> findChildElements(Set<String> names, Predicate<ElementWrapper> predicate) {
        Map<String, ElementWrapper> result = new HashMap<>();
        for (var it = getChildElementsAsIterator(); result.size() < names.size() && it.hasNext(); ) {
            var e = it.next();
            if (names.contains(e.getNodeName()) && predicate.test(e)) {
                result.put(e.getNodeName(), e);
            }
        }
        return result;
    }

    public Map<String, String> firstElementsText(Set<String> names) {
        return findChildElements(names, e -> e.getTextContentTrimmed().isPresent())
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getTextContentTrimmed().orElseThrow()));
    }
}
