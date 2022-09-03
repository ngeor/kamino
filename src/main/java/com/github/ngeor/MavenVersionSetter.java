package com.github.ngeor;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

public class MavenVersionSetter implements VersionSetter {
    private final Path currentDirectory;

    public MavenVersionSetter(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    @Override
    public void bumpVersion(String version) throws IOException {
        XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        File pomXml = currentDirectory.resolve("pom.xml").toFile();
        File newPomXml = Files.createTempFile("pom", ".xml").toFile();
        PathStack path = new PathStack();
        try (
            FileInputStream fis = new FileInputStream(pomXml);
            FileOutputStream fos = new FileOutputStream(newPomXml)
        ) {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(fis);
            XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(fos);
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                XMLEvent xmlWriteEvent = xmlEvent;
                if (xmlEvent.isEndElement()) {
                    path.pop();
                } else if (xmlEvent.isStartElement()) {
                    path.add(xmlEvent.asStartElement().getName().getLocalPart());
                } else if (
                    xmlEvent.isCharacters()
                        && path.isAtProjectVersion()) {
                    xmlWriteEvent = xmlEventFactory.createCharacters(version);
                }
                xmlEventWriter.add(xmlWriteEvent);
            }
            xmlEventWriter.close();
            xmlEventReader.close();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        if (!pomXml.delete()) {
            throw new IOException("Could not delete original pom.xml");
        }
        if (!newPomXml.renameTo(pomXml)) {
            throw new IOException("Could not rename pom.xml");
        }
    }

    private static class PathStack {
        private final LinkedList<String> path = new LinkedList<>();

        public void add(String name) {
            path.addLast(name);
        }

        public void pop() {
            path.removeLast();
        }

        public boolean isAtProjectVersion() {
            return path.size() == 2
                && "project".equals(path.getFirst())
                && "version".equals(path.getLast());
        }
    }
}
