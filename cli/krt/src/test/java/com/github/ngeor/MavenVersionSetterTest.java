package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

class MavenVersionSetterTest {
    @TempDir
    private Path tempDirectory;

    private MavenVersionSetter mavenVersionSetter;

    @BeforeEach
    void setup() {
        mavenVersionSetter = new MavenVersionSetter(tempDirectory);
    }

    @Test
    void testSetter() throws IOException, ParserConfigurationException, SAXException {
        // arrange
        final String schemaLocation = "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd";
        Files.writeString(
                tempDirectory.resolve("pom.xml"),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" "
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xsi:schemaLocation=\"" + schemaLocation + "\">"
                        + "  <modelVersion>4.0.0</modelVersion>\n"
                        + "  <groupId>com.github.ngeor</groupId>\n"
                        + "  <artifactId>checkstyle-rules</artifactId>\n"
                        + "  <version>6.1.0-SNAPSHOT</version>\n"
                        + "  <name>checkstyle-rules</name>"
                        + "</project>");

        // act
        mavenVersionSetter.bumpVersion("6.1.0");

        // assert
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document =
                documentBuilder.parse(tempDirectory.resolve("pom.xml").toFile());
        Element documentElement = document.getDocumentElement();
        String ns = "http://maven.apache.org/POM/4.0.0";
        assertEquals(
                "6.1.0",
                documentElement.getElementsByTagNameNS(ns, "version").item(0).getTextContent());
        assertEquals(
                "4.0.0",
                documentElement
                        .getElementsByTagNameNS(ns, "modelVersion")
                        .item(0)
                        .getTextContent());
    }
}
