package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.PROJECT;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.DomRuntimeException;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.Validate;

public class PomRepository {
    private final Map<MavenCoordinates, Map<ResolutionPhase, DocumentWrapper>> map = new HashMap<>();

    public MavenCoordinates load(String xmlContents) {
        Validate.notBlank(xmlContents, "xmlContents is required");
        final DocumentWrapper document;
        try {
            document = Objects.requireNonNull(DocumentWrapper.parseString(xmlContents));
        } catch (DomRuntimeException ex) {
            throw new RuntimeException("Cannot parse xmlContents", ex);
        }
        ElementWrapper documentElement = document.getDocumentElement();
        Validate.isTrue(
                PROJECT.equals(documentElement.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                documentElement.getNodeName(),
                PROJECT);
        Map<String, String> coordinates = documentElement.firstElementsText(Set.of(GROUP_ID, ARTIFACT_ID, VERSION));
        MavenCoordinates mavenCoordinates =
                new MavenCoordinates(coordinates.get(GROUP_ID), coordinates.get(ARTIFACT_ID), coordinates.get(VERSION));
        // TODO if possible to resolve them via the parent document, then it should be allowed
        validateCoordinates(mavenCoordinates);
        Validate.validState(!isKnown(mavenCoordinates), "Document %s is already loaded", mavenCoordinates.format());
        map.put(mavenCoordinates, new EnumMap<>(Map.of(ResolutionPhase.UNRESOLVED, document)));
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
        ResolutionPhase phase = getResolutionPhase(coordinates);
        if (phase == ResolutionPhase.UNRESOLVED) {
            DocumentWrapper document = getDocument(coordinates, ResolutionPhase.UNRESOLVED);
            ParentPom parentPom = ParentPom.fromDocument(document).orElse(null);
            if (parentPom == null) {
                // no parent pom, nothing to do
                map.get(coordinates).put(ResolutionPhase.PARENT_RESOLVED, document);
            } else {
                Validate.validState(
                        !parentPom.coordinates().hasMissingFields(),
                        "Document %s has incomplete parent coordinates",
                        coordinates.format());
                // prepare child document
                // TODO modify PomMerger to avoid cloning of current document
                DocumentWrapper cloneChild = document.deepClone();
                cloneChild.getDocumentElement().removeChildNodesByName("parent");
                // prepare parent document
                DocumentWrapper parent = resolveParent(parentPom);
                DocumentWrapper cloneParent = parent.deepClone();
                // perform merge
                new PomMerger.DocumentMerge().mergeIntoLeft(cloneParent, cloneChild);
                map.get(coordinates).put(ResolutionPhase.PARENT_RESOLVED, cloneParent);
            }
        }
        return getDocument(coordinates, ResolutionPhase.PARENT_RESOLVED);
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

    private DocumentWrapper resolveParent(ParentPom parentPom) {
        MavenCoordinates parentCoordinates = parentPom.coordinates();
        Validate.validState(isKnown(parentCoordinates), "Parent document %s is unknown", parentCoordinates.format());
        return resolveParent(parentCoordinates);
    }

    private static void validateCoordinates(MavenCoordinates coordinates) {
        Validate.validState(coordinates != null && !coordinates.hasMissingFields(), "Missing maven coordinates");
    }
}
