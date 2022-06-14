package com.github.ngeor.yak4jdom;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper for a Document.
 */
public class DocumentWrapper {
    private final Document document;

    public DocumentWrapper(Document document) {
        this.document = document;
    }

    /**
     * Parses the given XML file.
     */
    public static DocumentWrapper parse(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            return new DocumentWrapper(documentBuilder.parse(file));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    /**
     * Parses the given input stream.
     */
    public static DocumentWrapper parse(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            return new DocumentWrapper(documentBuilder.parse(inputStream));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public ElementWrapper getDocumentElement() {
        return new ElementWrapper(document.getDocumentElement());
    }

    public ElementWrapper createElement(String elementName) {
        return new ElementWrapper(document.createElement(elementName));
    }

    /**
     * Writes the document to a file.
     */
    public void write(File file) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            try (FileWriter fileWriter = new FileWriter(file)) {
                StreamResult streamResult = new StreamResult(fileWriter);
                transformer.transform(domSource, streamResult);
            }
        } catch (IOException | TransformerException ex) {
            throw new DomRuntimeException(ex);
        }
    }
}
