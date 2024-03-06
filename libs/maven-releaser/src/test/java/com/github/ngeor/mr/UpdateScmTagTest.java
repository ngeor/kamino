package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Test;

class UpdateScmTagTest {
    @Test
    void test() {
        // arrange
        String xml =
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <scm>
                    <tag>HEAD</tag>
                </scm>
            </project>
            """;
        DocumentWrapper document = DocumentWrapper.parseString(xml);

        // act
        new UpdateScmTag("1.0").accept(document);

        // assert
        assertThat(document.writeToString())
                .isEqualToNormalizingNewlines(
                        """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <scm>
                    <tag>1.0</tag>
                </scm>
            </project>
            """);
    }
}
