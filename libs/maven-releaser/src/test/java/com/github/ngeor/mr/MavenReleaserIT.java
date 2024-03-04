package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.InitOption;
import com.github.ngeor.git.User;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MavenReleaserIT {
    @TempDir
    private Path remoteRoot;

    @TempDir
    private Path monorepoRoot;

    private Git git;
    private MavenReleaser releaser;
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

    @SuppressWarnings("FieldCanBeLocal")
    private final String validChildPomWithParentContents =
            """
        <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0-SNAPSHOT</version>
            <name>foo</name>
            <description>The library</description>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <relativePath>..</relativePath>
            </parent>
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
    void beforeEach() throws IOException, ProcessFailedException {
        // create remote git
        Git remoteGit = new Git(remoteRoot.toFile());
        remoteGit.init("trunk", InitOption.BARE);

        // clone local git
        git = new Git(monorepoRoot.toFile());
        git.clone("file://" + remoteRoot.toAbsolutePath());
        git.configureIdentity(new User("John Doe", "no-reply@acme.com"));

        // so that the default branch can be determined
        git.symbolicRef("refs/remotes/origin/HEAD", "refs/remotes/origin/trunk");

        Files.createDirectory(monorepoRoot.resolve("lib"));
        releaser = new MavenReleaser(monorepoRoot.toFile(), "lib", Defaults.defaultFormatOptions());
    }

    @Test
    void testHappyFlow() throws IOException, ProcessFailedException {
        // arrange
        Files.writeString(monorepoRoot.resolve("pom.xml"), validParentPomContents);
        Path childPomPath = monorepoRoot.resolve("lib").resolve("pom.xml");
        Files.writeString(childPomPath, validChildPomContents);
        git.addAll();
        git.commit("chore: Added pom.xml");
        git.push();

        // act
        releaser.prepareRelease(new SemVer(1, 0, 0), false);

        // assert
        new DocumentAssertions(childPomPath).hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.1.0-SNAPSHOT"));
        List<Commit> commits = git.revList(null).toList();
        assertThat(commits.stream().map(Commit::summary))
                .containsExactly(
                        "release(lib): switching to development version 1.1.0-SNAPSHOT",
                        "release(lib): releasing 1.0.0",
                        "chore: Added pom.xml");
        assertThat(commits.stream().map(Commit::tag)).containsExactly(null, "lib/v1.0.0", null);
        git.checkout("lib/v1.0.0");
        new DocumentAssertions(childPomPath).hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.0.0"));
    }

    @Test
    void testHappyFlowWithParent() throws IOException, ProcessFailedException {
        // arrange
        Files.writeString(monorepoRoot.resolve("pom.xml"), validParentPomContents);
        Path childPomPath = monorepoRoot.resolve("lib").resolve("pom.xml");
        Files.writeString(childPomPath, validChildPomWithParentContents);
        git.addAll();
        git.commit("chore: Added pom.xml");
        git.push();

        // act
        releaser.prepareRelease(new SemVer(1, 0, 0), false);

        // assert
        new DocumentAssertions(childPomPath)
                .hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.1.0-SNAPSHOT"))
                .hasParentPom()
                .doesNotHaveModules()
                .hasScmTag("HEAD");
        List<Commit> commits = git.revList(null).toList();
        assertThat(commits.stream().map(Commit::summary))
                .containsExactly(
                        "release(lib): switching to development version 1.1.0-SNAPSHOT",
                        "release(lib): releasing 1.0.0",
                        "chore: Added pom.xml");
        assertThat(commits.stream().map(Commit::tag)).containsExactly(null, "lib/v1.0.0", null);
        git.checkout("lib/v1.0.0");
        new DocumentAssertions(childPomPath)
                .hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.0.0"))
                .doesNotHaveParentPom()
                .doesNotHaveModules()
                .hasScmTag("lib/v1.0.0");
    }

    @Test
    void testSnapshotsAreNotAllowed() throws IOException, ProcessFailedException {
        // arrange
        String parentContents =
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>parent</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
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
            </project>
            """;
        String childContents =
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <artifactId>lib</artifactId>
                <name>lib</name>
                <description>Cool library</description>
                <version>1.1-SNAPSHOT</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <relativePath>..</relativePath>
                </parent>
                <scm>
                    <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                    <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                    <tag>HEAD</tag>
                    <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                </scm>
                <dependencies>
                    <dependency>
                        <groupId>com.acme</groupId>
                        <artifactId>express</artifactId>
                        <version>0.1.2-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </project>
            """;
        Files.writeString(monorepoRoot.resolve("pom.xml"), parentContents);
        Path childPomPath = monorepoRoot.resolve("lib").resolve("pom.xml");
        Files.writeString(childPomPath, childContents);
        git.addAll();
        git.commit("chore: Added pom.xml");
        git.push();

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("Snapshot version 0.1.2-SNAPSHOT is not allowed");
    }

    @SuppressWarnings("UnusedReturnValue")
    record DocumentAssertions(DocumentWrapper document) {
        public DocumentAssertions(Path path) {
            this(DocumentWrapper.parse(path.toFile()));
        }

        public DocumentAssertions hasCoordinates(MavenCoordinates expected) {
            assertThat(DomHelper.coordinates(document)).isEqualTo(expected);
            return this;
        }

        public DocumentAssertions hasParentPom() {
            assertThat(document.getDocumentElement().firstElement(ElementNames.PARENT))
                    .isPresent();
            return this;
        }

        public DocumentAssertions doesNotHaveParentPom() {
            assertThat(document.getDocumentElement().firstElement(ElementNames.PARENT))
                    .isEmpty();
            return this;
        }

        public DocumentAssertions doesNotHaveModules() {
            assertThat(document.getDocumentElement().firstElement(ElementNames.MODULES))
                    .isEmpty();
            return this;
        }

        public DocumentAssertions hasScmTag(String expected) {
            assertThat(document.getDocumentElement()
                            .findChildElements("scm")
                            .flatMap(e -> e.findChildElements("tag"))
                            .flatMap(ElementWrapper::getTextContentTrimmedAsStream))
                    .containsExactly(expected);
            return this;
        }
    }

    @Nested
    class PomValidationIT {

        @Test
        void testModelVersionIsRequired() throws IOException, ProcessFailedException {
            testValidation(
                            removeElement(validParentPomContents, "modelVersion"),
                            removeElement(validChildPomContents, "modelVersion"))
                    .hasMessage("Element 'modelVersion' not found under 'project'");
        }

        @Test
        void testGroupIdIsRequired() throws IOException, ProcessFailedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "groupId"))
                    .hasMessage("Cannot resolve coordinates, parent element is missing");
        }

        @Test
        void testArtifactIdIsRequired() throws IOException, ProcessFailedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "artifactId"))
                    .hasMessageContaining("artifactId");
        }

        @Test
        void testVersionIsRequired() throws IOException, ProcessFailedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "version"))
                    .hasMessage("Cannot resolve coordinates, parent element is missing");
        }

        @Test
        void testNameIsRequired() throws IOException, ProcessFailedException {
            testValidation(validParentPomContents, validChildPomContents.replaceAll("<name>foo</name>", ""))
                    .hasMessageContaining("name");
        }

        @Test
        void testDescriptionIsRequired() throws IOException, ProcessFailedException {
            testMissingDescription();
        }

        @Test
        void testLicensesIsRequired() throws IOException, ProcessFailedException {
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
        void testScmConnectionIsRequired() throws IOException, ProcessFailedException {
            testValidation(validParentPomContents, removeElement(validChildPomContents, "connection"))
                    .hasMessage("Element 'connection' not found under 'project/scm'");
        }

        private AbstractThrowableAssert<?, ? extends Throwable> testValidation(
                String invalidParentPomContents, String invalidChildPomContents)
                throws IOException, ProcessFailedException {
            // arrange
            Files.writeString(monorepoRoot.resolve("pom.xml"), invalidParentPomContents);
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), invalidChildPomContents);
            git.addAll();
            git.commit("chore: Added pom.xml");
            git.push();

            // act and assert
            return assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false));
        }

        private void testMissingDescription() throws IOException, ProcessFailedException {
            String childElementName = "description";
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

    @Nested
    class GitValidationIT {
        @Test
        void testDefaultBranch() throws IOException, ProcessFailedException {
            // arrange
            Files.writeString(monorepoRoot.resolve("pom.xml"), validParentPomContents);
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), validChildPomContents);
            git.addAll();
            git.commit("chore: Added pom.xml");

            // act
            git.checkoutNewBranch("develop");

            // assert
            assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                    .hasMessage("repo was not on default branch (expected trunk, found develop)");
        }

        @Test
        void testNoUntrackedFiles() throws IOException, ProcessFailedException {
            // arrange
            addCommitWithInvalidXml();
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), validChildPomContents);
            git.addAll();
            git.commit("Fixed pom issues");

            // act
            Files.writeString(monorepoRoot.resolve("pom2.xml"), validChildPomContents);

            // assert
            assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                    .hasMessage("repo has untracked files");
        }

        @Test
        void testNoStagedFiles() throws IOException, ProcessFailedException {
            // arrange
            addCommitWithInvalidXml();
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), validChildPomContents);

            // act
            git.addAll();

            // act and assert
            assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                    .hasMessage("repo has staged files");
        }

        @Test
        void testNoModifiedFiles() throws IOException, ProcessFailedException {
            // arrange
            addCommitWithInvalidXml();

            // act
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), validChildPomContents);

            // act and assert
            assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                    .hasMessage("repo has modified files");
        }

        private void addCommitWithInvalidXml() throws IOException, ProcessFailedException {
            Files.writeString(monorepoRoot.resolve("pom.xml"), validParentPomContents);
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), "dummy");
            git.addAll();
            git.commit("Adding incorrect commit");
        }
    }
}
