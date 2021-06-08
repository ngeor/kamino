package com.github.ngeor.yak4jcli;

import java.util.Optional;
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
        NodeList childNodes = parentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE && childElementName.equals(item.getNodeName())) {
                return Optional.of((Element) item);
            }
        }
        return Optional.empty();
    }
}
