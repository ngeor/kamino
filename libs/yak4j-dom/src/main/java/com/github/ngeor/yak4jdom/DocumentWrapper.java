package com.github.ngeor.yak4jdom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A wrapper for a Document.
 */
public class DocumentWrapper {
    private final Document document;

    public DocumentWrapper(Document document) {
        this.document = Objects.requireNonNull(document);
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

    public static DocumentWrapper parseString(String input) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
            return parse(inputStream);
        } catch (IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public ElementWrapper getDocumentElement() {
        return new ElementWrapper(document.getDocumentElement());
    }

    /**
     * Writes the document to a file.
     */
    public void write(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            write(fileWriter);
        } catch (IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public void write(Writer writer) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);
        } catch (TransformerException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public String writeToString() {
        try (StringWriter writer = new StringWriter()) {
            write(writer);
            return writer + System.lineSeparator();
        } catch (IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public void indent() {
        getDocumentElement().indent();
    }

    public DocumentWrapper deepClone() {
        return new DocumentWrapper((Document) document.cloneNode(true));
    }
}
