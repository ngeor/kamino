package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
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

    public DocumentWrapper effectivePomViaMaven() throws IOException, InterruptedException {
        File output = File.createTempFile("pom", ".xml");
        try {
            processHelper.run("help:effective-pom", "-Doutput=" + output.getAbsolutePath());
            return DocumentWrapper.parse(output);
        } finally {
            output.delete();
        }
    }

    public DocumentWrapper effectivePom() {
        final DocumentWrapper document = effectivePomNgResolveParent();

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

    private DocumentWrapper effectivePomNgResolveParent() {
        final DocumentWrapper document = DocumentWrapper.parse(pomFile);

        // any parent pom?
        ElementWrapper parent =
                document.getDocumentElement().firstElement("parent").orElse(null);
        if (parent != null) {
            String groupId = parent.firstElementText("groupId");
            String artifactId = parent.firstElementText("artifactId");
            String version = parent.firstElementText("version");
            File parentPomFile = new File(System.getProperty("user.home"))
                    .toPath()
                    .resolve(".m2")
                    .resolve("repository")
                    .resolve(groupId.replace('.', '/'))
                    .resolve(artifactId)
                    .resolve(version)
                    .resolve(artifactId + "-" + version + ".pom")
                    .toFile();

            if (!parentPomFile.isFile()) {
                throw new UnsupportedOperationException("Installing missing Maven pom not supported: " + parentPomFile);
            }

            Maven parentMaven = new Maven(parentPomFile);
            DocumentWrapper parentResolved = parentMaven.effectivePomNgResolveParent();

            merge(parentResolved, document);
        }

        return document;
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

    public void install() throws IOException, InterruptedException {
        processHelper.runInheritIO("install");
    }

    private void merge(DocumentWrapper parent, DocumentWrapper child) {
        mergeProject(parent.getDocumentElement(), child.getDocumentElement());
    }

    private void mergeProject(ElementWrapper parent, ElementWrapper child) {
        mergeProperties(parent, child);
        List.of("modelVersion", "groupId", "artifactId", "version", "packaging")
                .forEach(name -> mergeSingleOccurringPlainTextElement(parent, child, name));
    }

    private void mergeProperties(ElementWrapper parent, ElementWrapper child) {
        parent.findChildElements("properties")
                .flatMap(ElementWrapper::getChildElements)
                .forEach(p -> {
                    ElementWrapper childProperties = child.ensureChild("properties");
                    if (childProperties
                            .findChildElements(p.getNodeName())
                            .findAny()
                            .isPresent()) {
                        // child property already exists, so it wins
                    } else {
                        childProperties.ensureChildText(p.getNodeName(), p.getTextContent());
                    }
                });
    }

    private void mergeSingleOccurringPlainTextElement(ElementWrapper parent, ElementWrapper child, String nodeName) {
        if (child.firstElement(nodeName).isPresent()) {
            // child wins
            return;
        }
        parent.findChildElements(nodeName).forEach(p -> {
            child.ensureChildText(nodeName, p.getTextContent());
        });
    }
}
