package com.github.ngeor.maven.resolve;

import static com.github.ngeor.maven.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.ElementNames.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.DomRuntimeException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PomRepositoryTest {
    private final StubResolver resolver = new StubResolver();
    private final PomRepository pomRepository = new PomRepository(resolver);

    @ParameterizedTest
    @NullAndEmptySource
    void loadEmpty(String xmlContents) {
        assertThatThrownBy(() -> pomRepository.loadAndResolveParent(xmlContents))
                .hasMessage("xmlContents is required");
    }

    @Test
    void loadInvalidXml() {
        assertThatThrownBy(() -> pomRepository.loadAndResolveParent("oops")).isInstanceOf(DomRuntimeException.class);
    }

    @Test
    void loadIncorrectRootElement() {
        assertThatThrownBy(() -> pomRepository.loadAndResolveParent("<oops />"))
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
        assertThatThrownBy(() -> pomRepository.loadAndResolveParent(xmlContents))
                .hasMessageStartingWith("Missing coordinates");
    }

    @Test
    void load() throws IOException {
        String xmlContents =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>""";
        MavenCoordinates result =
                pomRepository.loadAndResolveParent(xmlContents).coordinates();
        assertThat(result).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
    }

    @Test
    void resolveDocumentWithoutParent() throws IOException {
        String xmlContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";

        // act
        MavenCoordinates coordinates =
                pomRepository.loadAndResolveParent(xmlContents).coordinates();

        // assert
        assertThat(pomRepository.getResolutionPhase(coordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
    }

    @Test
    void resolveDocumentWithoutParentTwice() throws IOException {
        // arrange
        String xmlContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";

        // act
        LoadResult a = pomRepository.loadAndResolveParent(xmlContents);
        LoadResult b = pomRepository.loadAndResolveParent(xmlContents);
        assertThat(a).isNotNull().isEqualTo(b);
        assertThat(a.document()).isNotNull().isSameAs(b.document());
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
        assertThatThrownBy(() -> pomRepository.loadAndResolveParent(childContents))
                .hasMessage("Document com.acme:bar:1.1 has incomplete parent coordinates");
    }

    @Test
    void resolveDocumentWithParent() throws IOException {
        String parentContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        MavenCoordinates parentCoordinates =
                pomRepository.loadAndResolveParent(parentContents).coordinates();
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
        LoadResult loadResult = pomRepository.loadAndResolveParent(childContents);
        MavenCoordinates childCoordinates = loadResult.coordinates();
        DocumentWrapper document = loadResult.document();

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
    void resolveDocumentWithParentDynamically() throws IOException {
        String parentContents =
                """
        <project>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
        </project>""";
        resolver.setContents(parentContents);

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
        LoadResult loadResult = pomRepository.loadAndResolveParent(childContents);

        // assert
        MavenCoordinates childCoordinates = loadResult.coordinates();
        assertThat(pomRepository.getResolutionPhase(parentCoordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
        assertThat(pomRepository.getResolutionPhase(childCoordinates)).isEqualTo(ResolutionPhase.PARENT_RESOLVED);
    }

    private static final class StubResolver implements Resolver {
        private String contents;

        @Override
        public Input resolve(Input child, ParentPom parentPom) {
            return new StringInput(getContents());
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }
    }
}
