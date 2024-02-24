package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PomRepositoryTest {
    private final PomRepository pomRepository = new PomRepository();

    @ParameterizedTest
    @NullAndEmptySource
    void loadEmpty(String xmlContents) {
        assertThatThrownBy(() -> pomRepository.load(xmlContents)).hasMessage("xmlContents is required");
    }

    @Test
    void loadInvalidXml() {
        assertThatThrownBy(() -> pomRepository.load("oops")).hasMessage("Cannot parse xmlContents");
    }

    @Test
    void loadIncorrectRootElement() {
        assertThatThrownBy(() -> pomRepository.load("<oops />"))
                .hasMessage("Unexpected root element 'oops' (expected 'project')");
    }

    @ParameterizedTest
    @ValueSource(strings = {GROUP_ID, ARTIFACT_ID, VERSION})
    void loadMissingGroupId(String missingElement) {
        String xmlContents =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>"""
                        .replaceAll(String.format("<%s>.+?</%s>", missingElement, missingElement), "");
        assertThatThrownBy(() -> pomRepository.load(xmlContents)).hasMessage("Missing maven coordinates");
    }

    @Test
    void load() {
        String xmlContents =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>""";
        MavenCoordinates result = pomRepository.load(xmlContents);
        assertThat(result).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
    }

    @Test
    void loadTwiceIsNotAllowed() {
        String xmlContents =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>""";
        pomRepository.load(xmlContents);
        assertThatThrownBy(() -> pomRepository.load(xmlContents))
                .hasMessage("Document com.acme:foo:1.0 is already loaded");
    }

    @Test
    void getResolutionPhaseUnresolved() {
        String xmlContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates coordinates = pomRepository.load(xmlContents);
        assertThat(pomRepository.getResolutionPhase(coordinates)).isEqualTo(ResolutionPhase.UNRESOLVED);
    }

    @Test
    void resolveParentUnknownDocument() {
        assertThatThrownBy(() -> pomRepository.resolveParent(new MavenCoordinates("com.acme", "foo", "1.2")))
                .hasMessage("Document com.acme:foo:1.2 is unknown");
    }

    @Test
    void resolveDocumentWithoutParent() {
        String xmlContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates coordinates = pomRepository.load(xmlContents);

        // act
        pomRepository.resolveParent(coordinates);

        // assert
        assertThat(pomRepository.getResolutionPhase(coordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
    }

    @Test
    void resolveDocumentWithoutParentTwiceFails() {
        String xmlContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates coordinates = pomRepository.load(xmlContents);
        pomRepository.resolveParent(coordinates);
        assertThatThrownBy(() -> pomRepository.resolveParent(coordinates))
                .hasMessage("Document com.acme:foo:1.0 is already resolved");
    }
}
