package com.github.ngeor.maven;

import com.github.ngeor.ProcessFailedException;
import com.github.ngeor.ProcessHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class Maven {
    private final ProcessHelper processHelper;
    private final File pomFile;

    public Maven(File pomFile) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        this.pomFile = Objects.requireNonNull(pomFile);
        this.processHelper = new ProcessHelper(pomFile.getParentFile(), cmd, "-B", "-ntp", "--file", pomFile.getName());
    }

    public void sortPom() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort");
    }

    public void clean() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("clean");
    }

    public void verify() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("verify");
    }

    public DocumentWrapper effectivePomViaMaven() throws IOException, InterruptedException, ProcessFailedException {
        File output = File.createTempFile("pom", ".xml");
        try {
            effectivePomViaMaven(output);
            return DocumentWrapper.parse(output);
        } finally {
            output.delete();
        }
    }

    public void effectivePomViaMaven(File output) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("help:effective-pom", "-Doutput=" + output.getAbsolutePath());
    }

    public DocumentWrapper effectivePom(List<ParentPom> parentPoms) {
        final DocumentWrapper document = effectivePomNgResolveParent(parentPoms);

        // resolve properties
        ElementWrapper properties =
                document.getDocumentElement().firstElement("properties").orElse(null);
        if (properties != null) {
            // collect unresolved properties
            Map<String, String> unresolvedProperties = properties
                    .getChildElements()
                    .collect(Collectors.toMap(ElementWrapper::getNodeName, ElementWrapper::getTextContent));

            // resolve them
            Map<String, String> resolvedProperties = PropertyResolver.resolve(unresolvedProperties);

            // update the DOM recursively
            resolveProperties(document.getDocumentElement(), resolvedProperties);
        }

        return document;
    }

    public DocumentWrapper effectivePomNgResolveParent(List<ParentPom> parentPoms) {
        final DocumentWrapper document = DocumentWrapper.parse(pomFile);
        final ParentPom parentPom = ParentPom.fromDocument(document).orElse(null);
        if (parentPom == null) {
            return document;
        }

        parentPoms.add(parentPom);

        final File parentPomFile = resolveParentPomFile(parentPom);
        Maven parentMaven = new Maven(parentPomFile);
        // recursion
        DocumentWrapper parentResolved = parentMaven.effectivePomNgResolveParent(parentPoms);
        // remove parent element from document
        document.getDocumentElement().removeChildNodesByName("parent");
        return new PomMerger().withParent(parentResolved).mergeChild(document);
    }

    private File resolveParentPomFile(ParentPom parentPom) {
        return parentPom.relativePath() == null
                ? parentPomFileFromLocalRepository(parentPom)
                : parentPomFileFromRelativePath(parentPom);
    }

    private File parentPomFileFromLocalRepository(ParentPom parentPom) {
        File parentPomFile = new File(System.getProperty("user.home"))
                .toPath()
                .resolve(".m2")
                .resolve("repository")
                .resolve(parentPom.coordinates().groupId().replace('.', '/'))
                .resolve(parentPom.coordinates().artifactId())
                .resolve(parentPom.coordinates().version())
                .resolve(parentPom.coordinates().artifactId() + "-"
                        + parentPom.coordinates().version() + ".pom")
                .toFile();

        if (!parentPomFile.isFile()) {
            throw new UnsupportedOperationException("Installing missing Maven pom not supported: " + parentPomFile);
        }
        return parentPomFile;
    }

    private File parentPomFileFromRelativePath(ParentPom parentPom) {
        File parentPomFile = pomFile.toPath()
                .getParent()
                .resolve(parentPom.relativePath())
                .resolve("pom.xml")
                .toFile();
        if (!parentPomFile.isFile()) {
            throw new UncheckedIOException(
                    new FileNotFoundException("Parent pom not found at " + parentPom.relativePath()));
        }
        return parentPomFile;
    }

    private void resolveProperties(ElementWrapper element, Map<String, String> resolvedProperties) {
        for (Iterator<Node> it = element.getChildNodesAsIterator(); it.hasNext(); ) {
            Node node = it.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                resolveProperties(new ElementWrapper((Element) node), resolvedProperties);
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                node.setTextContent(PropertyResolver.resolve(node.getTextContent(), resolvedProperties::get));
            }
        }
    }

    public void install() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("install");
    }

    public void setVersion(MavenCoordinates moduleCoordinates, String newVersion)
            throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run(
                "versions:set",
                String.format("-DgroupId=%s", moduleCoordinates.groupId()),
                String.format("-DartifactId=%s", moduleCoordinates.artifactId()),
                String.format("-DoldVersion=%s", moduleCoordinates.version()),
                String.format("-DnewVersion=%s", newVersion));
    }
}