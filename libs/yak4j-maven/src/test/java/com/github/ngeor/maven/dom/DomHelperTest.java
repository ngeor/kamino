package com.github.ngeor.maven.dom;

import static com.github.ngeor.maven.dom.ElementNames.ARTIFACT_ID;
import static com.github.ngeor.maven.dom.ElementNames.GROUP_ID;
import static com.github.ngeor.maven.dom.ElementNames.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DomHelperTest {
    @Nested
    class GetParentPom {
        @Test
        void withParentPom() {
            String input =
                    """
                    <project>
                        <parent>
                            <groupId>com.acme</groupId>
                            <artifactId>foo</artifactId>
                            <version>1.0-SNAPSHOT</version>
                            <relativePath>../libs</relativePath>
                        </parent>
                    </project>
                    """;
            DocumentWrapper document = DocumentWrapper.parseString(input);
            Optional<ParentPom> result = DomHelper.getParentPom(document);
            assertThat(result)
                    .contains(new ParentPom(new MavenCoordinates("com.acme", "foo", "1.0-SNAPSHOT"), "../libs"));
        }

        @Test
        void withoutParentPom() {
            String input = """
                <project>
                </project>
                """;
            DocumentWrapper document = DocumentWrapper.parseString(input);
            Optional<ParentPom> result = DomHelper.getParentPom(document);
            assertThat(result).isEmpty();
        }

        @Test
        void withWhitespaceInElements() {
            String input =
                    """
                    <project>
                        <parent>
                            <groupId>
                                com.acme
                            </groupId>
                            <artifactId>
                                foo
                            </artifactId>
                            <version>
                                1.0-SNAPSHOT
                            </version>
                        </parent>
                    </project>
                    """;
            DocumentWrapper document = DocumentWrapper.parseString(input);
            Optional<ParentPom> result = DomHelper.getParentPom(document);
            assertThat(result).contains(new ParentPom(new MavenCoordinates("com.acme", "foo", "1.0-SNAPSHOT"), null));
        }
    }

    @Nested
    class Coordinates {
        private String xmlContents;

        private MavenCoordinates act() {
            return DomHelper.coordinates(DocumentWrapper.parseString(xmlContents));
        }

        @ParameterizedTest
        @ValueSource(strings = {GROUP_ID, ARTIFACT_ID, VERSION})
        void missingCoordinate(String missingElement) {
            xmlContents =
                    """
                <project>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>1.0</version>
                </project>"""
                            .replaceAll(String.format("<%s>.+?</%s>", missingElement, missingElement), "");
            assertThatThrownBy(this::act).hasMessageStartingWith("Cannot resolve coordinates");
        }

        @Test
        void coordinatesAlreadyPresent() {
            // arrange
            xmlContents =
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
            </project>""";

            // act
            MavenCoordinates result = act();

            // assert
            assertThat(result).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
        }

        @Test
        void artifactIdMissingIsFatal() {
            // arrange
            xmlContents =
                    """
            <project>
                <groupId>com.acme</groupId>
                <version>1.0</version>
                <parent>
                    <groupId>com.bar</groupId>
                    <artifactId>bar</artifactId>
                    <version>2.0</version>
                </parent>
            </project>""";

            // act and assert
            assertThatThrownBy(this::act).hasMessage("Cannot resolve coordinates, artifactId is missing");
        }

        @Test
        void resolveGroupIdFromParent() {
            // arrange
            xmlContents =
                    """
            <project>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <parent>
                    <groupId>com.bar</groupId>
                    <artifactId>bar</artifactId>
                    <version>2.0</version>
                </parent>
            </project>""";

            // act
            MavenCoordinates result = act();

            // assert
            assertThat(result).isEqualTo(new MavenCoordinates("com.bar", "foo", "1.0"));
        }

        @Test
        void resolveVersionFromParent() {
            // arrange
            xmlContents =
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <parent>
                    <groupId>com.bar</groupId>
                    <artifactId>bar</artifactId>
                    <version>2.0</version>
                </parent>
            </project>""";

            // act
            MavenCoordinates result = act();

            // assert
            assertThat(result).isEqualTo(new MavenCoordinates("com.acme", "foo", "2.0"));
        }

        @Test
        void resolveVersionFromParentWithMissingParentGroupId() {
            // arrange
            xmlContents =
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <parent>
                    <artifactId>bar</artifactId>
                    <version>2.0</version>
                </parent>
            </project>""";

            // act and assert
            assertThatThrownBy(this::act).hasMessage("groupId is missing from parent coordinates");
        }

        @Test
        void resolveVersionFromParentWithMissingParentArtifactId() {
            // arrange
            xmlContents =
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <parent>
                    <groupId>com.bar</groupId>
                    <version>2.0</version>
                </parent>
            </project>""";

            // act and assert
            assertThatThrownBy(this::act).hasMessage("artifactId is missing from parent coordinates");
        }

        @Test
        void resolveVersionFromParentWithMissingParentVersion() {
            // arrange
            xmlContents =
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <parent>
                    <groupId>com.bar</groupId>
                    <artifactId>bar</artifactId>
                </parent>
            </project>""";

            // act and assert
            assertThatThrownBy(this::act).hasMessage("version is missing from parent coordinates");
        }

        @Test
        void resolveVersionFromParentWithMissingParent() {
            // arrange
            xmlContents =
                    """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
            </project>""";

            // act and assert
            assertThatThrownBy(this::act).hasMessage("Cannot resolve coordinates, parent element is missing");
        }
    }
}
