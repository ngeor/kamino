package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.PROJECT;
import static com.github.ngeor.maven.ElementNames.VERSION;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.DomRuntimeException;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;

public class PomRepository {
    private final Map<MavenCoordinates, DocumentWrapper> unresolved = new HashMap<>();
    private final Map<MavenCoordinates, DocumentWrapper> resolved = new HashMap<>();
    private final Map<MavenCoordinates, ResolutionPhase> coordinatesToResolutionPhase = new HashMap<>();

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
        Validate.isTrue(coordinates.size() == 3, "Missing maven coordinates");
        MavenCoordinates mavenCoordinates =
                new MavenCoordinates(coordinates.get(GROUP_ID), coordinates.get(ARTIFACT_ID), coordinates.get(VERSION));
        mavenCoordinates.requireAllFields();
        Validate.validState(
                !unresolved.containsKey(mavenCoordinates), "Document %s is already loaded", mavenCoordinates.format());
        unresolved.put(mavenCoordinates, document);
        coordinatesToResolutionPhase.put(mavenCoordinates, ResolutionPhase.UNRESOLVED);
        return mavenCoordinates;
    }

    public ResolutionPhase getResolutionPhase(MavenCoordinates coordinates) {
        return coordinatesToResolutionPhase.get(Objects.requireNonNull(coordinates));
    }

    public void resolveParent(MavenCoordinates coordinates) {
        Validate.validState(
                !resolved.containsKey(Objects.requireNonNull(coordinates)),
                "Document %s is already resolved",
                coordinates.format());
        DocumentWrapper document = Objects.requireNonNull(
                unresolved.get(coordinates), String.format("Document %s is unknown", coordinates.format()));
        ParentPom parentPom = ParentPom.fromDocument(document).orElse(null);
        if (parentPom == null) {
            // no parent pom, nothing to do
            resolved.put(coordinates, document);
            coordinatesToResolutionPhase.put(coordinates, ResolutionPhase.PARENT_RESOLVED);
            return;
        }
        throw new NotImplementedException();
    }
}
