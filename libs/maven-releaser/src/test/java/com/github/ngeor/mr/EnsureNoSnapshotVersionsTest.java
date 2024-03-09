package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Test;

class EnsureNoSnapshotVersionsTest {
    private final String xml =
            """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>2.0</version>
                </parent>
                <dependencies>
                    <dependency>
                        <groupId>com.acme</groupId>
                        <artifactId>bar</artifactId>
                        <version>3.0</version>
                    </dependency>
                </dependencies>
            </project>
            """;

    @Test
    void allReleases() {
        // arrange
        DocumentWrapper document = DocumentWrapper.parseString(xml);

        // act and assert
        assertThatNoException().isThrownBy(() -> EnsureNoSnapshotVersions.INSTANCE.accept(document));
    }

    @Test
    void projectVersionIsSnapshot() {
        // arrange
        DocumentWrapper document = DocumentWrapper.parseString(xml.replace("1.0", "1.0-SNAPSHOT"));

        // act and assert
        assertThatThrownBy(() -> EnsureNoSnapshotVersions.INSTANCE.accept(document))
                .hasMessage("Snapshot version 1.0-SNAPSHOT is not allowed (project)");
    }

    @Test
    void parentVersionIsSnapshot() {
        // arrange
        DocumentWrapper document = DocumentWrapper.parseString(xml.replace("2.0", "2.0-SNAPSHOT"));

        // act and assert
        assertThatThrownBy(() -> EnsureNoSnapshotVersions.INSTANCE.accept(document))
                .hasMessage("Snapshot version 2.0-SNAPSHOT is not allowed (project/parent)");
    }

    @Test
    void dependencyVersionIsSnapshot() {
        // arrange
        DocumentWrapper document = DocumentWrapper.parseString(xml.replace("3.0", "3.0-SNAPSHOT"));

        // act and assert
        assertThatThrownBy(() -> EnsureNoSnapshotVersions.INSTANCE.accept(document))
                .hasMessage("Snapshot version 3.0-SNAPSHOT is not allowed (project/dependencies/dependency)");
    }
}
