package com.github.ngeor.maven.document.parent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.dom.ElementNames;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultParentLoaderTest {
    @TempDir
    private Path localRepository;

    private final DocumentLoaderFactory factory = FileDocumentLoader.asFactory();
    private final LocalRepositoryLocator localRepositoryLocator = () -> localRepository;
    private final DefaultParentLoader parentLoader = new DefaultParentLoader(factory, localRepositoryLocator);

    @Nested
    class LoadParent {
        @TempDir
        private File rootDir;

        private File childPom;

        @BeforeEach
        void beforeEach() throws IOException {
            File rootPom = new File(rootDir, "pom.xml");
            File childDir = new File(rootDir, "foo");
            childPom = new File(childDir, "pom.xml");
            Files.writeString(rootPom.toPath(), """
                <project>hello</project>""");
            Files.createDirectory(childDir.toPath());
        }

        @ParameterizedTest
        @ValueSource(strings = {"<relativePath>..</relativePath>", "<relativePath>../pom.xml</relativePath>", ""})
        void resolveRelative(String xmlRelativePath) throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
            <project>
                <parent>
                    %s
                </parent>
            </project>"""
                            .formatted(xmlRelativePath));
            DocumentLoader child = factory.createDocumentLoader(childPom);

            // act
            DocumentLoader parent = parentLoader.loadParent(child).orElseThrow();

            // assert
            assertThat(parent).isNotNull();
            assertThat(parent.loadDocument().getDocumentElement().getTextContent())
                    .isEqualTo("hello");
        }

        @Test
        void loadParentTwice() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
        <project>
            <parent>
            </parent>
        </project>""");
            DocumentLoader child = factory.createDocumentLoader(childPom);

            // act
            DocumentLoader parent1 = parentLoader.loadParent(child).orElseThrow();
            DocumentLoader parent2 = parentLoader.loadParent(child).orElseThrow();

            // assert
            assertThat(parent1).isNotNull();
            assertThat(parent2).isNotNull();
            assertThat(parent1).isNotSameAs(parent2);
        }

        @Test
        void noParentElement() throws IOException {
            // arrange
            Files.writeString(childPom.toPath(), """
                <project>
                </project>""");
            DocumentLoader child = factory.createDocumentLoader(childPom);

            // act
            DocumentLoader parent = parentLoader.loadParent(child).orElse(null);

            // assert
            assertThat(parent).isNull();
        }
    }

    @Nested
    class LoadParentWithLocalRepository {
        @TempDir
        private File childDir;

        private File childPom;

        @BeforeEach
        void beforeEach() throws IOException {
            Path rootPomDirectory = localRepository
                    .resolve("com")
                    .resolve("acme")
                    .resolve("foo")
                    .resolve("1.0");
            Files.createDirectories(rootPomDirectory);
            File rootPom = rootPomDirectory.resolve("foo-1.0.pom").toFile();
            Files.writeString(rootPom.toPath(), """
                <project>hello local repository</project>""");

            childPom = new File(childDir, "pom.xml");
        }

        @Test
        void fallbackToLocalRepositoryWhenRelativePathPointsToMissingFile() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
    <project>
        <parent>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0</version>
            <relativePath>../oops.xml</relativePath>
        </parent>
    </project>""");
            DocumentLoader child = factory.createDocumentLoader(childPom);

            // act
            DocumentLoader parent = parentLoader.loadParent(child).orElseThrow();

            // assert
            assertThat(parent).isNotNull();
            assertThat(parent.loadDocument().getDocumentElement().getTextContent())
                    .isEqualTo("hello local repository");
        }

        @Test
        void resolveViaLocalRepository() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </parent>
        </project>""");
            DocumentLoader child = factory.createDocumentLoader(childPom);

            // act
            DocumentLoader parent = parentLoader.loadParent(child).orElseThrow();

            // assert
            assertThat(parent).isNotNull();
            assertThat(parent.loadDocument().getDocumentElement().getTextContent())
                    .isEqualTo("hello local repository");
        }

        @ParameterizedTest
        @ValueSource(strings = {ElementNames.GROUP_ID, ElementNames.ARTIFACT_ID, ElementNames.VERSION})
        void missingCoordinate(String elementName) throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </parent>
        </project>"""
                            .replaceAll(String.format("<%s>.+</%s>", elementName, elementName), ""));
            DocumentLoader child = factory.createDocumentLoader(childPom);

            // act and assert
            assertThatThrownBy(() -> parentLoader.loadParent(child))
                    .hasMessage("%s is missing from parent coordinates", elementName);
        }

        @Test
        void fileNotFoundHappensOnlyOnLoadDocument() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>2.0</version>
            </parent>
        </project>""");
            DocumentLoader child = factory.createDocumentLoader(childPom);
            DocumentLoader parent = parentLoader.loadParent(child).orElseThrow();

            // act and assert
            assertThatThrownBy(parent::loadDocument)
                    .hasCauseInstanceOf(FileNotFoundException.class)
                    .hasMessageContaining("foo-2.0.pom");
        }
    }
}
