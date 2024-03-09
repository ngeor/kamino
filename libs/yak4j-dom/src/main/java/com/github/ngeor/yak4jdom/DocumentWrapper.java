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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A wrapper for a Document.
 */
public class DocumentWrapper {
    private static final LazyInitializer<DocumentBuilderFactory> lazyDocumentBuilderFactory =
            LazyInitializer.<DocumentBuilderFactory>builder()
                    .setInitializer(DocumentBuilderFactory::newInstance)
                    .get();
    private static final LazyInitializer<DocumentBuilder> lazyDocumentBuilder =
            LazyInitializer.<DocumentBuilder>builder()
                    .setInitializer(() -> lazyDocumentBuilderFactory.get().newDocumentBuilder())
                    .get();
    private static final LazyInitializer<TransformerFactory> lazyTransformerFactory =
            LazyInitializer.<TransformerFactory>builder()
                    .setInitializer(TransformerFactory::newInstance)
                    .get();
    private static final LazyInitializer<Transformer> lazyTransformer = LazyInitializer.<Transformer>builder()
            .setInitializer(() -> {
                Transformer transformer = lazyTransformerFactory.get().newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                return transformer;
            })
            .get();

    private final Document document;

    public DocumentWrapper(Document document) {
        this.document = Objects.requireNonNull(document);
    }

    /**
     * Parses the given XML file.
     */
    public static DocumentWrapper parse(File file) {
        try {
            DocumentBuilder documentBuilder = lazyDocumentBuilder.get();
            return new DocumentWrapper(documentBuilder.parse(file));
        } catch (ConcurrentException | SAXException | IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    /**
     * Parses the given input stream.
     */
    public static DocumentWrapper parse(InputStream inputStream) {
        try {
            DocumentBuilder documentBuilder = lazyDocumentBuilder.get();
            return new DocumentWrapper(documentBuilder.parse(inputStream));
        } catch (ConcurrentException | SAXException | IOException ex) {
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
            Transformer transformer = lazyTransformer.get();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);
            writer.append(System.lineSeparator());
        } catch (ConcurrentException | TransformerException | IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public String writeToString() {
        try (StringWriter writer = new StringWriter()) {
            write(writer);
            return writer.toString();
        } catch (IOException ex) {
            throw new DomRuntimeException(ex);
        }
    }

    public void indent(String indentation) {
        getDocumentElement().indent(indentation);
    }

    public DocumentWrapper deepClone() {
        return new DocumentWrapper((Document) document.cloneNode(true));
    }
}
