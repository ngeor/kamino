package com.github.ngeor.maven;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MavenDocument {
    private final DocumentWrapper document;
    private final File pomFile;

    public MavenDocument(File pomFile) {
        this.pomFile = pomFile;
        this.document = DocumentWrapper.parse(pomFile);
    }

    public static MavenDocument effectivePomWithoutResolvingProperties(Path pomXmlPath) {
        return effectivePomWithoutResolvingProperties(pomXmlPath.toFile());
    }

    public static MavenDocument effectivePomWithoutResolvingProperties(File pomXmlFile) {
        return new MavenDocument(pomXmlFile).effectivePomWithoutResolvingProperties();
    }

    @Deprecated
    public DocumentWrapper getDocument() {
        return document;
    }

    @Deprecated
    public ElementWrapper getDocumentElement() {
        return document.getDocumentElement();
    }

    public ParentPom parentPom() {
        return ParentPom.fromDocument(document).orElse(null);
    }

    public MavenCoordinates coordinates() {
        return MavenCoordinates.fromElement(document.getDocumentElement());
    }

    public Stream<MavenCoordinates> dependencies() {
        return document.getDocumentElement()
                .findChildElements("dependencies")
                .flatMap(dependencies -> dependencies.findChildElements("dependency"))
                .map(MavenCoordinates::fromElement);
    }

    public Stream<String> modules() {
        return document.getDocumentElement()
                .findChildElements("modules")
                .flatMap(e -> e.findChildElements("module"))
                .flatMap(e -> e.getTextContentTrimmed().stream());
    }

    private void removeParentPom() {
        document.getDocumentElement().removeChildNodesByName("parent");
    }

    public Map<String, String> properties() {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(ElementWrapper::getChildElements)
                .collect(Collectors.toMap(ElementWrapper::getNodeName, ElementWrapper::getTextContent));
    }

    public Optional<String> property(String name) {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(p -> p.findChildElements(name))
                .flatMap(p -> p.getTextContentTrimmed().stream())
                .findFirst();
    }

    public void resolveProperties(Map<String, String> resolvedProperties) {
        resolveProperties(document.getDocumentElement(), resolvedProperties);
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

    public Optional<String> modelVersion() {
        return topLevelElement("modelVersion");
    }

    public Optional<String> name() {
        return topLevelElement("name");
    }

    public Optional<String> description() {
        return topLevelElement("description");
    }

    private Optional<String> topLevelElement(String childElementName) {
        return document.getDocumentElement()
                .findChildElements(childElementName)
                .flatMap(p -> p.getTextContentTrimmed().stream())
                .findFirst();
    }

    public MavenDocument effectivePom() {
        return effectivePom(new ArrayList<>());
    }

    public MavenDocument effectivePom(List<ParentPom> parentPoms) {
        MavenDocument result = effectivePomWithoutResolvingProperties(parentPoms);
        result.resolveProperties();
        return result;
    }

    private void resolveProperties() {
        // resolve properties
        // collect unresolved properties
        Map<String, String> unresolvedProperties = properties();
        if (!unresolvedProperties.isEmpty()) {
            // resolve them
            Map<String, String> resolvedProperties = PropertyResolver.resolve(unresolvedProperties);

            // update the DOM recursively
            resolveProperties(resolvedProperties);
        }
    }

    public MavenDocument effectivePomWithoutResolvingProperties() {
        return effectivePomWithoutResolvingProperties(new ArrayList<>());
    }

    public MavenDocument effectivePomWithoutResolvingProperties(List<ParentPom> parentPoms) {
        final ParentPom parentPom = parentPom();
        if (parentPom == null) {
            // does not have a parent pom
            return this;
        }

        // add to list of visited parent poms
        parentPoms.add(parentPom);

        final File parentPomFile = resolveParentPomFile(parentPom);
        // recursion
        MavenDocument parentResolved =
                new MavenDocument(parentPomFile).effectivePomWithoutResolvingProperties(parentPoms);
        // remove parent element from document
        removeParentPom();
        return new PomMerger().withParent(parentResolved).mergeChild(this);
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
}
