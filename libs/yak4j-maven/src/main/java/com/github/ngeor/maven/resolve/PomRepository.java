package com.github.ngeor.maven.resolve;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.PROJECT;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PomRepository {
    private final Map<MavenCoordinates, Map<ResolutionPhase, DocumentWrapper>> map = new HashMap<>();
    private final Map<MavenCoordinates, Input> inputMap = new HashMap<>();
    private final Map<File, MavenCoordinates> fileMap = new HashMap<>();
    private final Map<MavenCoordinates, ParentPom> originalParentPom = new HashMap<>();
    private Resolver resolver = new DefaultResolver();

    public void setResolver(Resolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    public MavenCoordinates load(String xmlContents) {
        return load(new Input.StringInput(xmlContents));
    }

    public MavenCoordinates load(File pomFile) {
        return load(new Input.FileInput(pomFile));
    }

    public MavenCoordinates load(Input input) {
        Objects.requireNonNull(input);
        DocumentWrapper document = input.loadDocument();
        ElementWrapper documentElement = document.getDocumentElement();
        Validate.isTrue(
                PROJECT.equals(documentElement.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                documentElement.getNodeName(),
                PROJECT);
        Map<String, String> coordinates = documentElement.firstElementsText(Set.of(GROUP_ID, ARTIFACT_ID, VERSION));
        MavenCoordinates mavenCoordinates =
                new MavenCoordinates(coordinates.get(GROUP_ID), coordinates.get(ARTIFACT_ID), coordinates.get(VERSION));
        Validate.notBlank(mavenCoordinates.artifactId(), "Missing coordinates (artifactId) in %s", input);
        if (mavenCoordinates.hasMissingFields()) {
            // try to resolve parent
            ParentPom parentPom = DomHelper.getParentPom(document)
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Missing coordinates in document %s and no parent pom", input)));
            DocumentWrapper parentDoc = resolveParent(input, parentPom).deepClone();
            // TODO reduce duplication with other merge usage
            DocumentWrapper cloneChild = document.deepClone();
            cloneChild.getDocumentElement().removeChildNodesByName("parent");
            new PomMerger.DocumentMerge().mergeIntoLeft(parentDoc, cloneChild);
            // try to get the resolved coordinates
            coordinates = parentDoc.getDocumentElement().firstElementsText(Set.of(GROUP_ID, ARTIFACT_ID, VERSION));
            mavenCoordinates = new MavenCoordinates(
                    coordinates.get(GROUP_ID), coordinates.get(ARTIFACT_ID), coordinates.get(VERSION));
            Validate.validState(mavenCoordinates.isValid(), "Missing coordinates in %s after resolving parent", input);
            Validate.validState(
                    !isKnown(mavenCoordinates),
                    "Document %s is already loaded (trying to load %s, loaded=%s)",
                    mavenCoordinates.format(),
                    input,
                    inputMap);
            originalParentPom.put(mavenCoordinates, parentPom);
            map.put(
                    mavenCoordinates,
                    new EnumMap<>(Map.of(
                            ResolutionPhase.UNRESOLVED, document,
                            ResolutionPhase.PARENT_RESOLVED, parentDoc)));
        } else {
            Validate.validState(
                    !isKnown(mavenCoordinates),
                    "Document %s is already loaded (trying to load %s, loaded=%s)",
                    mavenCoordinates.format(),
                    input,
                    inputMap);
            map.put(mavenCoordinates, new EnumMap<>(Map.of(ResolutionPhase.UNRESOLVED, document)));
        }
        inputMap.put(mavenCoordinates, input);
        if (input instanceof Input.FileInput fi) {
            try {
                fileMap.put(fi.pomFile().getCanonicalFile(), mavenCoordinates);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        return mavenCoordinates;
    }

    public boolean isKnown(MavenCoordinates coordinates) {
        return map.containsKey(Objects.requireNonNull(coordinates));
    }

    public ResolutionPhase getResolutionPhase(MavenCoordinates coordinates) {
        Objects.requireNonNull(coordinates);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Validate.notEmpty(phaseMap, "Document %s is unknown", coordinates.format());
        return phaseMap.keySet().stream().max(Enum::compareTo).orElseThrow();
    }

    public DocumentWrapper resolveParent(MavenCoordinates coordinates) {
        validateCoordinates(coordinates);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Objects.requireNonNull(phaseMap, String.format("Document %s is unknown", coordinates.format()));
        return phaseMap.computeIfAbsent(ResolutionPhase.PARENT_RESOLVED, ignored -> {
            DocumentWrapper document = Objects.requireNonNull(
                    phaseMap.get(ResolutionPhase.UNRESOLVED), "Document should exist! Internal error!");
            ParentPom parentPom = DomHelper.getParentPom(document).orElse(null);
            if (parentPom == null) {
                // no parent pom, nothing to do
                return document;
            } else {
                Validate.validState(
                        parentPom.coordinates().isValid(),
                        "Document %s has incomplete parent coordinates",
                        coordinates.format());

                // prepare parent document
                DocumentWrapper parent = resolveParent(coordinates, parentPom);

                // prepare child document
                // TODO modify PomMerger to avoid cloning of current document
                DocumentWrapper cloneChild = document.deepClone();
                cloneChild.getDocumentElement().removeChildNodesByName("parent");
                originalParentPom.put(coordinates, parentPom);
                DocumentWrapper cloneParent = parent.deepClone();
                // perform merge
                new PomMerger.DocumentMerge().mergeIntoLeft(cloneParent, cloneChild);
                return cloneParent;
            }
        });
    }

    public ParentPom getOriginalParentPom(MavenCoordinates childCoordinates) {
        return originalParentPom.get(childCoordinates);
    }

    public DocumentWrapper getDocument(MavenCoordinates coordinates, ResolutionPhase phase) {
        validateCoordinates(coordinates);
        Objects.requireNonNull(phase);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Objects.requireNonNull(phaseMap, String.format("Document %s is unknown", coordinates.format()));
        DocumentWrapper document = phaseMap.get(phase);
        Objects.requireNonNull(document, String.format("Document %s is not in phase %s", coordinates.format(), phase));
        return document;
    }

    private DocumentWrapper resolveParent(MavenCoordinates childCoordinates, ParentPom parentPom) {
        validateCoordinates(childCoordinates);
        return resolveParent(inputMap.get(childCoordinates), parentPom);
    }

    private DocumentWrapper resolveParent(Input childInput, ParentPom parentPom) {
        Objects.requireNonNull(childInput);
        Objects.requireNonNull(parentPom);
        MavenCoordinates parentCoordinates = parentPom.coordinates();
        validateCoordinates(parentCoordinates);
        if (!isKnown(parentCoordinates)) {
            Input parentInput = resolver.resolve(childInput, parentPom);
            Validate.validState(
                    parentCoordinates.equals(load(parentInput)),
                    "Loaded parent document %s did not have expected coordinates %s",
                    parentInput,
                    parentCoordinates);
        }
        return resolveParent(parentCoordinates);
    }

    private static void validateCoordinates(MavenCoordinates coordinates) {
        Validate.validState(coordinates != null && coordinates.isValid(), "Missing coordinates");
    }

    public DocumentWrapper loadAndResolveProperties(File file) {
        MavenCoordinates coordinates = Objects.requireNonNullElseGet(fileMap.get(file), () -> load(file));
        return resolveProperties(coordinates);
    }

    public DocumentWrapper resolveProperties(MavenCoordinates coordinates) {
        validateCoordinates(coordinates);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Objects.requireNonNull(phaseMap, String.format("Document %s is unknown", coordinates.format()));
        return phaseMap.computeIfAbsent(ResolutionPhase.PROPERTIES_RESOLVED, ignored -> {
            DocumentWrapper parentResolved = resolveParent(coordinates);
            Map<String, String> unresolvedProperties = properties(parentResolved);
            if (unresolvedProperties == null || unresolvedProperties.isEmpty()) {
                return parentResolved;
            }

            // resolve them
            Map<String, String> resolvedProperties = PropertyResolver.resolve(unresolvedProperties);
            DocumentWrapper result = parentResolved.deepClone();
            resolveProperties(result, resolvedProperties);
            return result;
        });
    }

    // TODO copy pasted
    private static Map<String, String> properties(DocumentWrapper document) {
        return document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(ElementWrapper::getChildElements)
                .collect(Collectors.toMap(ElementWrapper::getNodeName, ElementWrapper::getTextContent));
    }

    private static void resolveProperties(DocumentWrapper document, Map<String, String> resolvedProperties) {
        resolveProperties(document.getDocumentElement(), resolvedProperties);
    }

    private static void resolveProperties(ElementWrapper element, Map<String, String> resolvedProperties) {
        for (Iterator<Node> it = element.getChildNodesAsIterator(); it.hasNext(); ) {
            Node node = it.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                resolveProperties(new ElementWrapper((Element) node), resolvedProperties);
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                node.setTextContent(PropertyResolver.resolve(node.getTextContent(), resolvedProperties::get));
            }
        }
    }

    public sealed interface Input {
        DocumentWrapper loadDocument();

        record StringInput(String contents) implements Input {
            public StringInput {
                Validate.notBlank(contents, "xmlContents is required");
            }

            @Override
            public DocumentWrapper loadDocument() {
                return DocumentWrapper.parseString(contents);
            }
        }

        record FileInput(File pomFile) implements Input {
            public FileInput {
                Objects.requireNonNull(pomFile);
                Validate.isTrue(pomFile.isFile(), "%s is not a file", pomFile);
            }

            @Override
            public DocumentWrapper loadDocument() {
                return DocumentWrapper.parse(pomFile);
            }
        }
    }

    @FunctionalInterface
    public interface Resolver {
        Input resolve(Input child, ParentPom parentPom);
    }
}