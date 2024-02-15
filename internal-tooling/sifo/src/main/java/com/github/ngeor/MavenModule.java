package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public final class MavenModule {
    private final File typeDirectory;
    private final File projectDirectory;
    private final File pomFile;
    private DocumentWrapper effectivePom;
    private final List<ParentPom> parentPoms = new ArrayList<>();

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

    public DocumentWrapper effectivePom() throws IOException, InterruptedException {
        if (effectivePom == null) {
            parentPoms.clear();
            Maven maven = new Maven(pomFile);
            effectivePom = maven.effectivePom(parentPoms);
        }

        return effectivePom;
    }

    public List<ParentPom> parentPoms() {
        return Collections.unmodifiableList(parentPoms);
    }

    public MavenCoordinates coordinates()
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        DocumentWrapper document = effectivePom();
        return new MavenCoordinates(
                document.getDocumentElement().firstElementText("groupId"),
                document.getDocumentElement().firstElementText("artifactId"),
                document.getDocumentElement().firstElementText("version"));
    }

    public Stream<MavenCoordinates> dependencies()
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        return effectivePom()
                .getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(dependency -> new MavenCoordinates(
                        dependency.firstElementText("groupId"),
                        dependency.firstElementText("artifactId"),
                        dependency.firstElementText("version")));
    }

    public Optional<String> calculateJavaVersion() throws IOException, InterruptedException {
        return effectivePom()
                .getDocumentElement()
                .firstElement("properties")
                .map(p -> p.firstElementText("maven.compiler.source"))
                .map(v -> "1.8".equals(v) ? "8" : v);
    }
}
