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
import org.apache.commons.lang3.Validate;

public class PomRepository {
    private final Map<MavenCoordinates, DocumentWrapper> unresolved = new HashMap<>();

    public void load(String xmlContents) {
        Validate.notBlank(xmlContents, "xmlContents is required");
        final DocumentWrapper document;
        try {
            document = Objects.requireNonNull(DocumentWrapper.parseString(xmlContents));
        } catch (DomRuntimeException ex) {
            throw new RuntimeException("Cannot parse xmlContents", ex);
        }
        ElementWrapper documentElement = document.getDocumentElement();
        Validate.isTrue(PROJECT.equals(documentElement.getNodeName()),
            "Unexpected root element '%s' (expected '%s')", documentElement.getNodeName(), PROJECT
        );

        Map<String, String> coordinates = documentElement.firstElementsText(Set.of(GROUP_ID, ARTIFACT_ID, VERSION));
        Validate.isTrue(coordinates.size() == 3, "Missing maven coordinates");
        MavenCoordinates mavenCoordinates = new MavenCoordinates(
            coordinates.get(GROUP_ID),
            coordinates.get(ARTIFACT_ID),
            coordinates.get(VERSION)
        );
        mavenCoordinates.requireAllFields();
        Validate.validState(!unresolved.containsKey(mavenCoordinates), "Document %s:%s:%s is already loaded", mavenCoordinates.groupId(), mavenCoordinates.artifactId(), mavenCoordinates.version());
        unresolved.put(mavenCoordinates, document);
    }
}
