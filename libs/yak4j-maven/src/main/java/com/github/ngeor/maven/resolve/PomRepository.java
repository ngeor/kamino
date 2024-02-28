package com.github.ngeor.maven.resolve;

import static com.github.ngeor.maven.ElementNames.PROJECT;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.function.FailableSupplier;

public class PomRepository {
    private final Map<MavenCoordinates, Map<ResolutionPhase, DocumentWrapper>> map = new HashMap<>();
    private final Map<MavenCoordinates, Input> coordinatesToInputMap = new HashMap<>();
    private final Map<Input, MavenCoordinates> inputToCoordinatesMap = new HashMap<>();
    private final Resolver resolver;

    public PomRepository(Resolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    public PomRepository() {
        this(new DefaultResolver());
    }

    public LoadResult loadAndResolveParent(File file) throws IOException {
        return loadAndResolveParent(new FileInput(file));
    }

    public LoadResult loadAndResolveParent(String contents) throws IOException {
        return loadAndResolveParent(new StringInput(contents));
    }

    public LoadResult loadAndResolveParent(Input input) throws IOException {
        return loadAndThen(input, this::resolveParent);
    }

    public LoadResult loadAndResolveProperties(File file) throws IOException {
        return loadAndResolveProperties(new FileInput(file));
    }

    public LoadResult loadAndResolveProperties(String contents) throws IOException {
        return loadAndResolveProperties(new StringInput(contents));
    }

    public LoadResult loadAndResolveProperties(Input input) throws IOException {
        return loadAndThen(input, this::resolveProperties);
    }

    private LoadResult loadAndThen(Input input, FailableFunction<MavenCoordinates, DocumentWrapper, IOException> action)
            throws IOException {
        Objects.requireNonNull(input);
        MavenCoordinates coordinates = inputToCoordinatesMap.get(input);
        if (coordinates == null) {
            coordinates = load(input);
        }
        DocumentWrapper document = action.apply(coordinates);
        return new LoadResult(coordinates, document);
    }

    private boolean isUnknown(MavenCoordinates coordinates) {
        return !map.containsKey(Objects.requireNonNull(coordinates));
    }

    @Deprecated
    public DocumentWrapper getDocument(MavenCoordinates coordinates, ResolutionPhase phase) {
        validateCoordinates(coordinates);
        Objects.requireNonNull(phase);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Objects.requireNonNull(phaseMap, String.format("Document %s is unknown", coordinates.format()));
        DocumentWrapper document = phaseMap.get(phase);
        Objects.requireNonNull(document, String.format("Document %s is not in phase %s", coordinates.format(), phase));
        return document;
    }

    private DocumentWrapper resolveParent(MavenCoordinates childCoordinates, ParentPom parentPom) throws IOException {
        validateCoordinates(childCoordinates);
        return resolveParent(coordinatesToInputMap.get(childCoordinates), parentPom);
    }

    private DocumentWrapper resolveParent(Input childInput, ParentPom parentPom) throws IOException {
        Objects.requireNonNull(childInput);
        Objects.requireNonNull(parentPom);
        MavenCoordinates parentCoordinates = parentPom.coordinates();
        validateCoordinates(parentCoordinates);
        if (isUnknown(parentCoordinates)) {
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

    private DocumentWrapper resolveParent(MavenCoordinates coordinates) throws IOException {
        validateCoordinates(coordinates);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Objects.requireNonNull(phaseMap, String.format("Document %s is unknown", coordinates.format()));
        return computeIfAbsent(phaseMap, ResolutionPhase.PARENT_RESOLVED, () -> {
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
                DocumentWrapper cloneParent = parent.deepClone();

                // perform merge
                new PomMerger.DocumentMerge().mergeIntoLeft(cloneParent, document);
                return cloneParent;
            }
        });
    }

    private DocumentWrapper resolveProperties(MavenCoordinates coordinates) throws IOException {
        validateCoordinates(coordinates);
        Map<ResolutionPhase, DocumentWrapper> phaseMap = map.get(coordinates);
        Objects.requireNonNull(phaseMap, String.format("Document %s is unknown", coordinates.format()));
        return computeIfAbsent(phaseMap, ResolutionPhase.PROPERTIES_RESOLVED, () -> {
            DocumentWrapper parentResolved = resolveParent(coordinates);
            Map<String, String> unresolvedProperties = DomHelper.getProperties(parentResolved);
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

    private static void resolveProperties(DocumentWrapper document, Map<String, String> resolvedProperties) {
        document.getDocumentElement()
                .transformTextNodes(text -> PropertyResolver.resolve(text, resolvedProperties::get));
    }

    private static <K, V, E extends Throwable> V computeIfAbsent(Map<K, V> map, K key, FailableSupplier<V, E> supplier)
            throws E {
        if (map.containsKey(key)) {
            return map.get(key);
        }

        V newValue = supplier.get();
        map.put(key, newValue);
        return newValue;
    }

    private MavenCoordinates load(Input input) throws IOException {
        Objects.requireNonNull(input);
        DocumentWrapper document = input.loadDocument();
        ElementWrapper documentElement = document.getDocumentElement();
        Validate.isTrue(
                PROJECT.equals(documentElement.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                documentElement.getNodeName(),
                PROJECT);
        MavenCoordinates coordinates = DomHelper.getCoordinates(documentElement);
        Validate.notBlank(coordinates.artifactId(), "Missing coordinates (artifactId) in %s", input);
        if (coordinates.hasMissingFields()) {
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
            coordinates = DomHelper.getCoordinates(parentDoc);
            Validate.validState(coordinates.isValid(), "Missing coordinates in %s after resolving parent", input);
            Validate.validState(
                    isUnknown(coordinates),
                    "Document %s is already loaded (trying to load %s, loaded=%s)",
                    coordinates.format(),
                    input,
                    coordinatesToInputMap);
            map.put(
                    coordinates,
                    new EnumMap<>(Map.of(
                            ResolutionPhase.UNRESOLVED, document,
                            ResolutionPhase.PARENT_RESOLVED, parentDoc)));
        } else {
            Validate.validState(
                    isUnknown(coordinates),
                    "Document %s is already loaded (trying to load %s, loaded=%s)",
                    coordinates.format(),
                    input,
                    coordinatesToInputMap);
            map.put(coordinates, new EnumMap<>(Map.of(ResolutionPhase.UNRESOLVED, document)));
        }
        coordinatesToInputMap.put(coordinates, input);
        inputToCoordinatesMap.put(input, coordinates);
        return coordinates;
    }
}
