package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XmlUtils {
    private XmlUtils() {}

    public static Document parse(File file) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder =
                DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        return documentBuilder.parse(file);
    }

    public static void write(Document document, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(file));
    }

    public static void setChildText(Element parentElement, String tag, String text) {
        ensureChild(parentElement, tag).setTextContent(text);
    }

    public static Element ensureChild(Element parentElement, String tag) {
        Element childElement;
        NodeList elements = parentElement.getElementsByTagName(tag);
        if (elements.getLength() == 0) {
            childElement = parentElement.getOwnerDocument().createElement(tag);
            parentElement.appendChild(childElement);
        } else {
            childElement = (Element) elements.item(0);
        }
        return childElement;
    }
}
