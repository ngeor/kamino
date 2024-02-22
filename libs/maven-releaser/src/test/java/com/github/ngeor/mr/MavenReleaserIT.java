package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.Commit;
import com.github.ngeor.Git;
import com.github.ngeor.ProcessFailedException;
import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.MavenDocument;
import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MavenReleaserIT {
    private Path monorepoRoot;
    private Git git;
    private final String validParentPomContents =
            """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """;
    private final String validChildPomContents =
            """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <name>foo</name>
                <description>The library</description>
                <licenses>
                    <license>
                        <name>MIT</name>
                        <url>https://opensource.org/licenses/MIT</url>
                    </license>
                </licenses>
                <developers>
                    <developer>
                        <name>Nikolaos Georgiou</name>
                        <email>nikolaos.georgiou@gmail.com</email>
                    </developer>
                </developers>
                <scm>
                    <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                    <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                    <tag>HEAD</tag>
                    <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                </scm>
            </project>
            """;

    @BeforeEach
    void beforeEach() throws IOException, ProcessFailedException, InterruptedException {
        monorepoRoot = Files.createTempDirectory("test");
        Files.createDirectory(monorepoRoot.resolve("lib"));
        git = new Git(monorepoRoot.toFile());
        git.init();
    }

    @AfterEach
    void afterEach() throws IOException {
        FileUtils.deleteDirectory(monorepoRoot.toFile());
    }

    @Test
    void testHappyFlow() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(monorepoRoot.resolve("pom.xml"), validParentPomContents);
        Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), validChildPomContents);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act
        releaser.prepareRelease(new SemVer(1, 0, 0), false);

        // assert
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(
                monorepoRoot.resolve("lib").resolve("pom.xml"));
        assertThat(mavenDocument.coordinates()).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.1.0-SNAPSHOT"));
        List<Commit> commits = git.revList(null).toList();
        assertThat(commits.stream().map(Commit::summary))
                .containsExactly(
                        "release(lib): switching to development version 1.1.0-SNAPSHOT",
                        "release(lib): releasing 1.0.0",
                        "chore: Added pom.xml");
        assertThat(commits.stream().map(Commit::tag)).containsExactly(null, "lib/v1.0.0", null);
    }

    @Nested
    class PomValidationTest {

        @Test
        void testModelVersionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(
                removeElement(validParentPomContents, "modelVersion"),
                removeElement(validChildPomContents, "modelVersion"))
                .hasMessage("Element 'modelVersion' not found under 'project'");
        }

        @Test
        void testGroupIdIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "groupId"))
                .hasMessageContaining("groupId");
        }

        @Test
        void testArtifactIdIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "artifactId"))
                .hasMessageContaining("artifactId");
        }

        @Test
        void testVersionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "version"))
                .hasMessageContaining("version");
        }

        @Test
        void testNameIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(validParentPomContents, validChildPomContents.replaceAll("<name>foo</name>", ""))
                .hasMessageContaining("name");
        }

        @Test
        void testDescriptionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testMissingTopLevelElement("description");
        }

        @Test
        void testLicensesIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(
                validParentPomContents,
                """
                    <project>
                        <modelVersion>4.0.0</modelVersion>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0-SNAPSHOT</version>
                        <name>foo</name>
                        <description>Some library</description>
                    </project>
                    """)
                .hasMessageContaining("licenses");
        }

        @Test
        void testScmConnectionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "connection"))
                .hasMessage("Element 'connection' not found under 'project/scm'");
        }

        private AbstractThrowableAssert<?, ? extends Throwable> testValidation(
            String invalidParentPomContents, String invalidChildPomContents)
            throws IOException, ProcessFailedException, InterruptedException {
            // arrange
            Files.writeString(monorepoRoot.resolve("pom.xml"), invalidParentPomContents);
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), invalidChildPomContents);
            git.addAll();
            git.commit("chore: Added pom.xml");

            MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

            // act and assert
            return assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false));
        }

        private void testMissingTopLevelElement(String childElementName)
            throws IOException, ProcessFailedException, InterruptedException {
            // test missing element
            testValidation(validParentPomContents, removeElement(validChildPomContents, childElementName))
                .hasMessage(String.format("Element '%s' not found under 'project'", childElementName));
            // test empty element
            testValidation(
                validParentPomContents,
                validChildPomContents.replaceAll(
                    String.format("<%s>.+?</%s>", childElementName, childElementName),
                    String.format("<%s />", childElementName)))
                .hasMessage(String.format("Element 'project/%s' must have text content", childElementName));
        }

        private static String removeElement(String xml, String elementName) {
            return xml.replaceAll(String.format("<%s>.+?</%s>", elementName, elementName), "");
        }
    }
}
