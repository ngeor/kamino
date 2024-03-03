package com.github.ngeor.maven.document.repository;

import static com.github.ngeor.maven.dom.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.dom.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.dom.ElementNames.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.DomRuntimeException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PomRepositoryTest {
    private final PomRepository pomRepository = new PomRepository();

    @TempDir
    private Path tempDir;

    @Nested
    class ResolveWithParentRecursively {

        private DocumentLoader resolveWithParentRecursively(String contents) {
            Objects.requireNonNull(contents);
            try {
                Files.writeString(tempDir.resolve("pom.xml"), contents);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return pomRepository.resolveWithParentRecursively(
                    tempDir.resolve("pom.xml").toFile());
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = {" ", "oops"})
        void invalidXml(String value) {
            assertThatThrownBy(() -> resolveWithParentRecursively(value)).isInstanceOf(DomRuntimeException.class);
        }

        @Test
        void incorrectRootElement() {
            assertThatThrownBy(() -> resolveWithParentRecursively("<oops />"))
                    .hasMessage("Unexpected root element 'oops' (expected 'project')");
        }

        @ParameterizedTest
        @ValueSource(strings = {GROUP_ID, ARTIFACT_ID, VERSION})
        void missingCoordinate(String missingElement) {
            String xmlContents =
                    """
                    <project>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                    </project>"""
                            .replaceAll(String.format("<%s>.+?</%s>", missingElement, missingElement), "");
            assertThatThrownBy(() -> resolveWithParentRecursively(xmlContents))
                    .hasMessageStartingWith("Cannot resolve coordinates");
        }

        @Test
        void withoutParent() {
            String xmlContents =
                    """
                    <project>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                    </project>""";

            // act
            DocumentLoader result = resolveWithParentRecursively(xmlContents);

            // assert
            assertThat(result).isNotNull();
            assertThat(result)
                    .as("Should return an equal Input instance if no parent exists")
                    .isEqualTo(pomRepository.createDocumentLoader(
                            tempDir.resolve("pom.xml").toFile()));
            MavenCoordinates coordinates = result.coordinates();
            assertThat(coordinates).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
        }

        @Test
        void withoutParentTwice() {
            // arrange
            String xmlContents =
                    """
                    <project>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                    </project>""";

            // act
            DocumentLoader a = resolveWithParentRecursively(xmlContents);
            DocumentLoader b = resolveWithParentRecursively(xmlContents);
            assertThat(a).isNotNull().isSameAs(b);
            assertThat(a.loadDocument()).isNotNull().isSameAs(b.loadDocument());
        }

        @ParameterizedTest
        @ValueSource(strings = {"<groupId>a</groupId>", "<artifactId>b</artifactId>", "<version>c</version>"})
        void withParentWithIncompleteCoordinates(String lineToRemove) {
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
            assertThatThrownBy(() -> resolveWithParentRecursively(childContents))
                    .hasMessageContaining("is missing from parent coordinates");
        }

        @Test
        void withParent() throws IOException {
            String parentContents =
                    """
                    <project>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                    </project>""";
            Files.writeString(tempDir.resolve("parent.xml"), parentContents);

            String childContents =
                    """
                    <project>
                        <parent>
                            <groupId>com.acme</groupId>
                            <artifactId>foo</artifactId>
                            <version>1.0</version>
                            <relativePath>parent.xml</relativePath>
                        </parent>
                        <groupId>com.acme</groupId>
                        <artifactId>bar</artifactId>
                        <version>1.1</version>
                    </project>""";

            // act
            DocumentLoader loadResult = resolveWithParentRecursively(childContents);
            DocumentWrapper document = loadResult.loadDocument();

            // assert
            assertThat(document.getDocumentElement().firstElement("parent"))
                    .as("Resolved document should not have parent element anymore")
                    .isEmpty();
            assertThat(pomRepository
                            .createDocumentLoader(tempDir.resolve("pom.xml").toFile())
                            .loadDocument()
                            .getDocumentElement()
                            .firstElement("parent"))
                    .as("Unresolved child document should still have parent element")
                    .isPresent();
            assertThat(pomRepository
                            .createDocumentLoader(tempDir.resolve("parent.xml").toFile())
                            .loadDocument())
                    .as("Unresolved and resolved parent document should point to the same document")
                    .isSameAs(pomRepository
                            .resolveWithParentRecursively(
                                    tempDir.resolve("parent.xml").toFile())
                            .loadDocument());
        }
    }

    @Nested
    class LoadAndResolveProperties {
        @Test
        void noProperties() throws IOException {
            // arrange
            Path pomXmlPath = tempDir.resolve("pom.xml");
            Files.writeString(
                    pomXmlPath,
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                </project>
                """);

            // act
            File pomXmlFile = pomXmlPath.toFile();
            DocumentWrapper doc =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();

            // assert
            assertThat(doc)
                    .isNotNull()
                    .isSameAs(pomRepository.createDocumentLoader(pomXmlFile).loadDocument())
                    .isSameAs(pomRepository
                            .resolveWithParentRecursively(pomXmlFile)
                            .loadDocument());
        }

        @Test
        void noPropertiesTwice() throws IOException {
            // arrange
            Path pomXmlPath = tempDir.resolve("pom.xml");
            Files.writeString(
                    pomXmlPath,
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                </project>
                """);

            // act
            File pomXmlFile = pomXmlPath.toFile();
            DocumentWrapper doc1 =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();
            DocumentWrapper doc2 =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();

            // assert
            assertThat(doc1)
                    .isNotNull()
                    .isSameAs(doc2)
                    .isSameAs(pomRepository.createDocumentLoader(pomXmlFile).loadDocument())
                    .isSameAs(pomRepository
                            .resolveWithParentRecursively(pomXmlFile)
                            .loadDocument());
        }

        @Test
        void noPropertiesReplaced() throws IOException {
            // arrange
            Path pomXmlPath = tempDir.resolve("pom.xml");
            Files.writeString(
                    pomXmlPath,
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                    <properties>
                        <color>blue</color>
                    </properties>
                </project>
                """);

            // act
            File pomXmlFile = pomXmlPath.toFile();
            DocumentWrapper doc =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();

            // assert
            assertThat(doc)
                    .isNotNull()
                    .isSameAs(pomRepository.createDocumentLoader(pomXmlFile).loadDocument())
                    .isSameAs(pomRepository
                            .resolveWithParentRecursively(pomXmlFile)
                            .loadDocument());
        }

        @Test
        void properties() throws IOException {
            // arrange
            Path pomXmlPath = tempDir.resolve("pom.xml");
            Files.writeString(
                    pomXmlPath,
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                    <properties>
                        <color>blue</color>
                        <model>deep ${color}</model>
                    </properties>
                </project>
                """);

            // act
            File pomXmlFile = pomXmlPath.toFile();
            DocumentWrapper doc =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();

            // assert
            assertThat(doc).isNotNull();
            assertThat(DomHelper.getProperty(doc, "color")).contains("blue");
            assertThat(DomHelper.getProperty(doc, "model")).contains("deep blue");
            DocumentWrapper oldDoc =
                    pomRepository.resolveWithParentRecursively(pomXmlFile).loadDocument();
            assertThat(DomHelper.getProperty(oldDoc, "color")).contains("blue");
            assertThat(DomHelper.getProperty(oldDoc, "model")).contains("deep ${color}");
        }

        @Test
        void propertiesTwice() throws IOException {
            // arrange
            Path pomXmlPath = tempDir.resolve("pom.xml");
            Files.writeString(
                    pomXmlPath,
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                    <properties>
                        <color>blue</color>
                        <model>deep ${color}</model>
                    </properties>
                </project>
                """);

            // act
            File pomXmlFile = pomXmlPath.toFile();
            DocumentWrapper doc1 =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();
            DocumentWrapper doc2 =
                    pomRepository.loadAndResolveProperties(pomXmlFile).resolveProperties();

            // assert
            assertThat(doc1).isNotNull().isSameAs(doc2);
        }

        @Test
        void test() throws IOException {
            Files.writeString(
                    tempDir.resolve("pom.xml"),
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>aggregator</artifactId>
                <version>1.0</version>
                <packaging>pom</packaging>
                <modules>
                    <module>grandparent</module>
                    <module>parent</module>
                    <module>child</module>
                </modules>
            </project>""");
            Files.createDirectory(tempDir.resolve("grandparent"));
            Files.writeString(
                    tempDir.resolve("grandparent").resolve("pom.xml"),
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>grandparent</artifactId>
                <version>2.0</version>
                <packaging>pom</packaging>
                <properties>
                    <java.version>11</java.version>
                </properties>
            </project>
            """);
            Files.createDirectory(tempDir.resolve("parent"));
            Files.writeString(
                    tempDir.resolve("parent").resolve("pom.xml"),
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>parent</artifactId>
                <version>3.0</version>
                <packaging>pom</packaging>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>grandparent</artifactId>
                    <version>2.0</version>
                    <relativePath>../grandparent</relativePath>
                </parent>
                <properties>
                    <java.version>11</java.version>
                </properties>
            </project>
            """);
            Files.createDirectory(tempDir.resolve("child"));
            Files.writeString(
                    tempDir.resolve("child").resolve("pom.xml"),
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>child</artifactId>
                <version>4.0</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>3.0</version>
                    <relativePath>../parent</relativePath>
                </parent>
            </project>
            """);

            assertThat(pomRepository.loadAndResolveProperties(new File(tempDir.toFile(), "pom.xml")))
                    .isNotNull();
            for (String module : List.of("parent", "child", "grandparent")) {
                assertThat(pomRepository.loadAndResolveProperties(
                                new File(new File(tempDir.toFile(), module), "pom.xml")))
                        .isNotNull();
            }
        }
    }
}
