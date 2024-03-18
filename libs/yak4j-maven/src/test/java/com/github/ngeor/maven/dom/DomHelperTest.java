package com.github.ngeor.maven.dom;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            assertThat(result).contains(new ParentPom("com.acme", "foo", "1.0-SNAPSHOT", "../libs"));
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
            assertThat(result).contains(new ParentPom("com.acme", "foo", "1.0-SNAPSHOT", null));
        }
    }
}
