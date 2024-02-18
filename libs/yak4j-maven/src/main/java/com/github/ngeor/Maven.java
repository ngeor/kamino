package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
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

    public void cleanRelease() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("release:clean");
    }

    public void prepareRelease(
            String tag, String releaseVersion, String developmentVersion, boolean dryRun, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {
        List<String> args = new ArrayList<>(List.of(
                "-Dtag=" + tag,
                "release:prepare",
                "-DreleaseVersion=" + releaseVersion,
                "-DdevelopmentVersion=" + developmentVersion,
                "-Dresume=false",
                "-DcheckModificationExcludeList=pom.xml"));
        if (dryRun) {
            args.add("-DdryRun=true");
        }
        if (!push) {
            args.add("-DpushChanges=false");
        }
        processHelper.runInheritIO(args.toArray(String[]::new));
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

    private DocumentWrapper effectivePomNgResolveParent(List<ParentPom> parentPoms) {
        final DocumentWrapper document = DocumentWrapper.parse(pomFile);

        // any parent pom?
        ElementWrapper parent =
                document.getDocumentElement().firstElement("parent").orElse(null);
        if (parent != null) {
            String groupId = parent.firstElementText("groupId");
            String artifactId = parent.firstElementText("artifactId");
            String version = parent.firstElementText("version");
            String relativePath = parent.firstElementText("relativePath");
            final File parentPomFile;
            if (relativePath == null) {
                parentPomFile = new File(System.getProperty("user.home"))
                        .toPath()
                        .resolve(".m2")
                        .resolve("repository")
                        .resolve(groupId.replace('.', '/'))
                        .resolve(artifactId)
                        .resolve(version)
                        .resolve(artifactId + "-" + version + ".pom")
                        .toFile();

                if (!parentPomFile.isFile()) {
                    throw new UnsupportedOperationException(
                            "Installing missing Maven pom not supported: " + parentPomFile);
                }
            } else {
                parentPomFile = pomFile.toPath()
                        .getParent()
                        .resolve(relativePath)
                        .resolve("pom.xml")
                        .toFile();
                if (!parentPomFile.isFile()) {
                    throw new UncheckedIOException(
                            new FileNotFoundException("Parent pom not found at " + relativePath));
                }
            }

            ParentPom parentPom = new ParentPom(new MavenCoordinates(groupId, artifactId, version), relativePath);
            parentPoms.add(parentPom);

            Maven parentMaven = new Maven(parentPomFile);
            DocumentWrapper parentResolved = parentMaven.effectivePomNgResolveParent(parentPoms);

            new PomMerger().merge(parentResolved, document);
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

    public void install() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("install");
    }
}
