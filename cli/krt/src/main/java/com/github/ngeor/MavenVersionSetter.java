package com.github.ngeor;

import com.github.ngeor.xml.XmlEvent;
import com.github.ngeor.xml.XmlEventType;
import com.github.ngeor.xml.XmlParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        File pomXml = currentDirectory.resolve("pom.xml").toFile();
        File newPomXml = Files.createTempFile("pom", ".xml").toFile();
        PathStack path = new PathStack();
        try (FileInputStream fis = new FileInputStream(pomXml);
                OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(newPomXml))) {
            XmlParser parser = new XmlParser(fis);
            var iterator = parser.iterator();
            while (iterator.hasNext()) {
                XmlEvent xmlEvent = iterator.next();
                XmlEvent xmlWriteEvent = xmlEvent;
                if (xmlEvent.isEndElement()) {
                    path.pop();
                } else if (xmlEvent.isStartElement()) {
                    path.add(xmlEvent.getNodeName());
                } else if (xmlEvent.isCharacters() && path.isAtProjectVersion()) {
                    xmlWriteEvent = new XmlEvent(version, XmlEventType.TEXT);
                }
                fos.write(xmlWriteEvent.getText());
            }
        }
        if (!pomXml.delete()) {
            throw new IOException("Could not delete original pom.xml");
        }
        if (!newPomXml.renameTo(pomXml)) {
            throw new IOException("Could not rename pom.xml");
        }
    }

    private static final class PathStack {
        private final LinkedList<String> path = new LinkedList<>();

        public void add(String name) {
            path.addLast(name);
        }

        public void pop() {
            path.removeLast();
        }

        public boolean isAtProjectVersion() {
            return path.size() == 2 && "project".equals(path.getFirst()) && "version".equals(path.getLast());
        }
    }
}
