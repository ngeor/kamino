package com.github.ngeor.yak4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * XML Utility.
 */
class XmlUtil {
    private final DocumentBuilder documentBuilder;

    XmlUtil() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    XmlUtil(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    /**
     * Gets the text contents of the elements at the given path.
     */
    List<String> getElementContents(Document document, String... path) {
        if (path == null || path.length <= 0) {
            throw new IllegalArgumentException("path");
        }

        List<String> result = new ArrayList<>();
        Element element = walkPath(document, path);
        if (element != null) {
            Node node = element.getFirstChild();
            while (node != null) {
                if (node.getNodeName().equals(path[path.length - 1])) {
                    result.add(node.getTextContent());
                }

                node = node.getNextSibling();
            }
        }

        return result;
    }

    List<String> getElementContents(File file, String... path) throws IOException, SAXException {
        Document document = documentBuilder.parse(file);
        return getElementContents(document, path);
    }

    List<String> getElementContents(InputStream inputStream, String... path) throws IOException, SAXException {
        Document document = documentBuilder.parse(inputStream);
        return getElementContents(document, path);
    }

    private Element walkPath(Document document, String... path) {
        Element element = document.getDocumentElement();
        int i = 0;
        while (i < path.length - 1 && element != null) {
            element = findElement(element, path[i++]);
        }

        return element;
    }

    private Element findElement(Element parent, String name) {
        Node node = parent.getFirstChild();
        boolean found = false;
        while (node != null && !found) {
            found = node.getNodeName().equals(name);
            if (!found) {
                node = node.getNextSibling();
            }
        }

        return found ? (Element) node : null;
    }
}
