package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Test;

class RemoveParentElementsTest {

    @Test
    void nothingIsRemoved() {
        // arrange
        String xml =
                """
            <project>
                <groupId>com.example</groupId>
            </project>
            """;

        // act
        DocumentWrapper document = act(xml);

        // assert
        assertThat(document.writeToString()).isEqualToNormalizingNewlines(xml);
    }

    @Test
    void parentIsRemoved() {
        // arrange
        String xml =
                """
            <project>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>1.0</version>
                </parent>
                <packaging>jar</packaging>
            </project>
            """;

        // act
        DocumentWrapper document = act(xml);

        // assert
        assertThat(document.writeToString())
                .isEqualToNormalizingNewlines(
                        """
            <project>
                <packaging>jar</packaging>
            </project>
            """);
    }

    @Test
    void modulesAreRemoved() {
        // arrange
        String xml =
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <modules>
                    <module>alpha</module>
                    <module>beta</module>
                </modules>
                <packaging>jar</packaging>
            </project>
            """;

        // act
        DocumentWrapper document = act(xml);

        // assert
        assertThat(document.writeToString())
                .isEqualToNormalizingNewlines(
                        """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <packaging>jar</packaging>
            </project>
            """);
    }

    private static DocumentWrapper act(String xml) {
        DocumentWrapper document = DocumentWrapper.parseString(xml);
        RemoveParentElements.INSTANCE.accept(document);
        document.indent("    ");
        return document;
    }
}
