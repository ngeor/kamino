package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Maven {
    private final ProcessHelper processHelper;
    private final File pomFile;

    public Maven(File pomFile) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        this.pomFile = Objects.requireNonNull(pomFile);
        this.processHelper = new ProcessHelper(pomFile.getParentFile(), cmd, "-B", "-ntp", "--file", pomFile.getName());
    }

    public void sortPom() throws IOException, InterruptedException {
        processHelper.run("-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort");
    }

    public void cleanRelease() throws IOException, InterruptedException {
        processHelper.run("release:clean");
    }

    public void prepareRelease(String tag, String releaseVersion, String developmentVersion)
            throws IOException, InterruptedException {
        processHelper.runInheritIO(
                "-Dtag=" + tag,
                "release:prepare",
                "-DreleaseVersion=" + releaseVersion,
                "-DdevelopmentVersion=" + developmentVersion);
    }

    public void clean() throws IOException, InterruptedException {
        processHelper.runInheritIO("clean");
    }

    public void verify() throws IOException, InterruptedException {
        processHelper.runInheritIO("verify");
    }

    public DocumentWrapper effectivePom() throws IOException, InterruptedException {
        File output = File.createTempFile("pom", ".xml");
        try {
            processHelper.run("help:effective-pom", "-Doutput=" + output.getAbsolutePath());
            return DocumentWrapper.parse(output);
        } finally {
            output.delete();
        }
    }

    public DocumentWrapper effectivePomNg() {
        DocumentWrapper document = DocumentWrapper.parse(pomFile);

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

    private void resolveProperties(ElementWrapper element, Map<String, String> resolvedProperties) {
        for (Iterator<Node> it = element.getChildNodesAsIterator(); it.hasNext(); ) {
            Node node = it.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                resolveProperties(new ElementWrapper((Element) node), resolvedProperties);
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                node.setTextContent(
                    PropertyResolver.resolve(node.getTextContent(), resolvedProperties::get)
                );
            }
        }
    }
}
