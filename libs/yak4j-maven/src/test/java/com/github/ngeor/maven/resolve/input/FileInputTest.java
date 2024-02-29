package com.github.ngeor.maven.resolve.input;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileInputTest {
    private final InputFactory factory = FileInput.asFactory();

    @Nested
    class Document {
        @TempDir
        private File rootDir;

        private File rootPom;

        @BeforeEach
        void beforeEach() throws IOException {
            rootPom = new File(rootDir, "pom.xml");
            Files.writeString(rootPom.toPath(), """
                <project>hello world</project>""");
        }

        @Test
        void loadDocument() {
            Input input = factory.load(rootPom);
            DocumentWrapper document = input.document();
            assertThat(document).isNotNull();
            assertThat(document.getDocumentElement().getTextContent()).isEqualTo("hello world");
        }

        @Test
        void loadDocumentTwiceReturnsDifferentInstance() {
            Input input = factory.load(rootPom);
            DocumentWrapper doc1 = input.document();
            DocumentWrapper doc2 = input.document();
            assertThat(doc1).isNotNull().isNotSameAs(doc2);
        }

        @Test
        void fileNotFound() {
            Input input = factory.load(new File(rootDir, "oops.xml"));
            assertThatThrownBy(input::document)
                    .hasCauseInstanceOf(FileNotFoundException.class)
                    .hasMessageContaining("oops.xml");
        }
    }

    @Nested
    @SuppressWarnings("java:S5976")
    class Coordinates {
        @TempDir
        private File childDir;

        private File childPom;

        @BeforeEach
        void beforeEach() {
            childPom = new File(childDir, "pom.xml");
        }

        @Test
        void coordinatesAlreadyPresent() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                    <parent>
                        <groupId>com.bar</groupId>
                        <artifactId>bar</artifactId>
                        <version>2.0</version>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act
            MavenCoordinates result = input.coordinates();

            // assert
            assertThat(result).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
        }

        @Test
        void artifactIdMissingIsFatal() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <version>1.0</version>
                    <parent>
                        <groupId>com.bar</groupId>
                        <artifactId>bar</artifactId>
                        <version>2.0</version>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act and assert
            assertThatThrownBy(input::coordinates).hasMessage("Cannot resolve coordinates, artifactId is missing");
        }

        @Test
        void resolveGroupIdFromParent() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                    <parent>
                        <groupId>com.bar</groupId>
                        <artifactId>bar</artifactId>
                        <version>2.0</version>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act
            MavenCoordinates result = input.coordinates();

            // assert
            assertThat(result).isEqualTo(new MavenCoordinates("com.bar", "foo", "1.0"));
        }

        @Test
        void resolveVersionFromParent() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <parent>
                        <groupId>com.bar</groupId>
                        <artifactId>bar</artifactId>
                        <version>2.0</version>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act
            MavenCoordinates result = input.coordinates();

            // assert
            assertThat(result).isEqualTo(new MavenCoordinates("com.acme", "foo", "2.0"));
        }

        @Test
        void resolveVersionFromParentWithMissingParentGroupId() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <parent>
                        <artifactId>bar</artifactId>
                        <version>2.0</version>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act and assert
            assertThatThrownBy(input::coordinates).hasMessage("groupId is missing from parent coordinates");
        }

        @Test
        void resolveVersionFromParentWithMissingParentArtifactId() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <parent>
                        <groupId>com.bar</groupId>
                        <version>2.0</version>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act and assert
            assertThatThrownBy(input::coordinates).hasMessage("artifactId is missing from parent coordinates");
        }

        @Test
        void resolveVersionFromParentWithMissingParentVersion() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <parent>
                        <groupId>com.bar</groupId>
                        <artifactId>bar</artifactId>
                    </parent>
                </project>""");
            Input input = factory.load(childPom);

            // act and assert
            assertThatThrownBy(input::coordinates).hasMessage("version is missing from parent coordinates");
        }

        @Test
        void resolveVersionFromParentWithMissingParent() throws IOException {
            // arrange
            Files.writeString(
                    childPom.toPath(),
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                </project>""");
            Input input = factory.load(childPom);

            // act and assert
            assertThatThrownBy(input::coordinates).hasMessage("Cannot resolve coordinates, parent element is missing");
        }
    }
}
