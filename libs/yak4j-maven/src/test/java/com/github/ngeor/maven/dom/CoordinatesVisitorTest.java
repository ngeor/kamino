package com.github.ngeor.maven.dom;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class CoordinatesVisitorTest {

    @Test
    void rootLevelAllFieldsPresent() {
        String xml =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
            </project>
            """;
        assertThat(act(xml)).containsExactly(new MavenCoordinates("com.acme", "foo", "1.0"));
    }

    @Test
    void rootLevelSomeFieldsPresent() {
        String xml =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
            </project>
            """;
        assertThat(act(xml)).containsExactly(new MavenCoordinates("com.acme", "foo", null));
    }

    @Test
    void rootLevelNoFieldsPresent() {
        String xml = """
            <project>
            </project>
            """;
        assertThat(act(xml)).isEmpty();
    }

    @Test
    void parentPom() {
        String xml =
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>2.0</version>
                    <relativePath>..</relativePath>
                </parent>
            </project>
            """;
        assertThat(act(xml))
                .containsExactly(
                        new MavenCoordinates("com.acme", "parent", "2.0"),
                        new MavenCoordinates("com.acme", "foo", "1.0"));
    }

    @Test
    void rootLevelAndParentPom() {
        String xml =
                """
        <project>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>parent</artifactId>
                <relativePath>..</relativePath>
            </parent>
        </project>
        """;
        assertThat(act(xml)).containsExactly(new MavenCoordinates("com.acme", "parent", null));
    }

    @Test
    void requireAllFields() {
        String xml =
                """
            <project>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>1.0</version>
                </parent>
                <artifactId>foo</artifactId>
                <version>2.0</version>
            </project>
            """;
        List<String> paths = new ArrayList<>();
        List<MavenCoordinates> coordinates = new ArrayList<>();
        CoordinatesVisitor visitor = new CoordinatesVisitor((e, c) -> {
            paths.add(e.path());
            coordinates.add(c);
        });
        visitor.visit(DocumentWrapper.parseString(xml));
        assertThat(paths).containsExactly("project/parent");
        assertThat(coordinates).containsExactly(new MavenCoordinates("com.acme", "parent", "1.0"));
    }

    private List<MavenCoordinates> act(String xml) {
        List<MavenCoordinates> list = new ArrayList<>();
        CoordinatesVisitor visitor = new CoordinatesVisitor(false, (element, coordinates) -> list.add(coordinates));
        DocumentWrapper document = DocumentWrapper.parseString(xml);
        visitor.visit(document);
        return list;
    }
}
