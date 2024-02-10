package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class MavenModule {
    private final File typeDirectory;
    private final File projectDirectory;
    private final File pomFile;
    private String effectivePomContents;

    public MavenModule(File typeDirectory, File projectDirectory, File pomFile) {
        this.typeDirectory = typeDirectory;
        this.projectDirectory = projectDirectory;
        this.pomFile = pomFile;
    }

    public String path() {
        return typeDirectory.getName() + "/" + projectDirectory.getName();
    }

    public File typeDirectory() {
        return typeDirectory;
    }

    public File projectDirectory() {
        return projectDirectory;
    }

    public File pomFile() {
        return pomFile;
    }

    public String effectivePom() throws IOException, InterruptedException {
        if (effectivePomContents == null) {
            File tempFile = File.createTempFile("pom", ".xml");
            try {
                Maven maven = new Maven(pomFile);
                maven.effectivePom(tempFile);
                effectivePomContents = Files.readString(tempFile.toPath());
            } finally {
                tempFile.delete();
            }
        }

        return effectivePomContents;
    }

    private Document effectivePomDocument()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        return XmlUtils.parse(effectivePom());
    }

    public MavenCoordinates coordinates()
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        Document document = effectivePomDocument();
        return new MavenCoordinates(
                XmlUtils.getChild(document.getDocumentElement(), "groupId").getTextContent(),
                XmlUtils.getChild(document.getDocumentElement(), "artifactId").getTextContent(),
                XmlUtils.getChild(document.getDocumentElement(), "version").getTextContent());
    }

    public Stream<MavenCoordinates> dependencies()
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        DocumentWrapper document = new DocumentWrapper(effectivePomDocument());
        return document.getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(dependency -> new MavenCoordinates(
                        dependency.firstElementText("groupId"),
                        dependency.firstElementText("artifactId"),
                        dependency.firstElementText("version")));
    }
}
