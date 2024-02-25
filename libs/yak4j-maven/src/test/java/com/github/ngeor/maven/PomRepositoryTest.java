package com.github.ngeor.maven;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.DomRuntimeException;
import org.apache.commons.lang3.Validate;
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
        assertThatThrownBy(() -> pomRepository.load("oops")).isInstanceOf(DomRuntimeException.class);
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
        assertThatThrownBy(() -> pomRepository.load(xmlContents)).hasMessageStartingWith("Missing coordinates");
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
                .hasMessageStartingWith("Document com.acme:foo:1.0 is already loaded");
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
    void resolveDocumentWithoutParentTwice() {
        String xmlContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates coordinates = pomRepository.load(xmlContents);
        DocumentWrapper document1 = pomRepository.resolveParent(coordinates);
        DocumentWrapper document2 = pomRepository.resolveParent(coordinates);
        assertThat(document1).isNotNull().isSameAs(document2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"<groupId>a</groupId>", "<artifactId>b</artifactId>", "<version>c</version>"})
    void resolveDocumentWithParentWithIncompleteCoordinates(String lineToRemove) {
        String childContents =
                """
    <project>
        <parent>
            <groupId>a</groupId>
            <artifactId>b</artifactId>
            <version>c</version>
        </parent>
        <groupId>com.acme</groupId>
        <artifactId>bar</artifactId>
        <version>1.1</version>
    </project>"""
                        .replace(lineToRemove, "");
        MavenCoordinates childCoordinates = pomRepository.load(childContents);
        assertThatThrownBy(() -> pomRepository.resolveParent(childCoordinates))
                .hasMessage("Document com.acme:bar:1.1 has incomplete parent coordinates");
    }

    @Test
    void resolveDocumentWithParent() {
        String parentContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates parentCoordinates = pomRepository.load(parentContents);
        String childContents =
                """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </parent>
            <groupId>com.acme</groupId>
            <artifactId>bar</artifactId>
            <version>1.1</version>
        </project>""";
        MavenCoordinates childCoordinates = pomRepository.load(childContents);

        // act
        DocumentWrapper document = pomRepository.resolveParent(childCoordinates);

        // assert
        assertThat(pomRepository.getResolutionPhase(parentCoordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
        assertThat(pomRepository.getResolutionPhase(childCoordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
        assertThat(document.getDocumentElement().firstElement("parent"))
                .as("Resolved document should not have parent element anymore")
                .isEmpty();
        assertThat(pomRepository
                        .getDocument(childCoordinates, ResolutionPhase.UNRESOLVED)
                        .getDocumentElement()
                        .firstElement("parent"))
                .as("Unresolved child document should still have parent element")
                .isPresent();
        assertThat(pomRepository.getDocument(parentCoordinates, ResolutionPhase.UNRESOLVED))
                .as("Unresolved and resolved parent document should point to the same document")
                .isSameAs(pomRepository.getDocument(parentCoordinates, ResolutionPhase.PARENT_RESOLVED));
    }

    @Test
    void loadDocumentWithUnknownParent() {
        // arrange
        MavenCoordinates parentCoordinates = new MavenCoordinates("com.acme", "foo", "1.0");
        String childContents =
                """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </parent>
            <groupId>com.acme</groupId>
            <artifactId>bar</artifactId>
            <version>1.1</version>
        </project>""";

        // act
        MavenCoordinates childCoordinates = pomRepository.load(childContents);

        // assert
        assertThat(childCoordinates).isEqualTo(new MavenCoordinates("com.acme", "bar", "1.1"));
        assertThat(pomRepository.isKnown(parentCoordinates)).isFalse();
        assertThat(pomRepository.getResolutionPhase(childCoordinates)).isEqualTo(ResolutionPhase.UNRESOLVED);
    }

    @Test
    void resolveDocumentWithParentDynamically() {
        String parentContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates parentCoordinates = new MavenCoordinates("com.acme", "foo", "1.0");
        String childContents =
                """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </parent>
            <groupId>com.acme</groupId>
            <artifactId>bar</artifactId>
            <version>1.1</version>
        </project>""";
        MavenCoordinates childCoordinates = pomRepository.load(childContents);
        pomRepository.setResolver((child, parentPom) -> {
            Validate.validState(parentCoordinates.equals(parentPom.coordinates()));
            return new PomRepository.Input.StringInput(parentContents);
        });

        // act
        pomRepository.resolveParent(childCoordinates);

        // assert
        assertThat(pomRepository.getResolutionPhase(parentCoordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
        assertThat(pomRepository.getResolutionPhase(childCoordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
    }
}
