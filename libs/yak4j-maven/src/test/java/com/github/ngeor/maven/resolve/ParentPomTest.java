package com.github.ngeor.maven.resolve;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ParentPomTest {
    @Test
    void fromDocument() {
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
        Optional<ParentPom> result = ParentPom.fromDocument(document);
        assertThat(result).contains(new ParentPom(new MavenCoordinates("com.acme", "foo", "1.0-SNAPSHOT"), "../libs"));
    }

    @Test
    void fromDocumentWithoutParentPom() {
        String input = """
        <project>
        </project>
        """;
        DocumentWrapper document = DocumentWrapper.parseString(input);
        Optional<ParentPom> result = ParentPom.fromDocument(document);
        assertThat(result).isEmpty();
    }

    @Test
    void fromDocumentWithWhitespaceInElements() {
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
        Optional<ParentPom> result = ParentPom.fromDocument(document);
        assertThat(result).contains(new ParentPom(new MavenCoordinates("com.acme", "foo", "1.0-SNAPSHOT"), null));
    }
}
