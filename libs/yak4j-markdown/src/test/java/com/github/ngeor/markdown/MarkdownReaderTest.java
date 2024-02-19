package com.github.ngeor.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class MarkdownReaderTest {
    @Test
    @Disabled("Line terminator issues")
    void testRead() {
        String input =
                """
            # Changelog

            whatever

            ## Unreleased

            something

            ## v1

            something else
            """;

        assertThat(MarkdownReader.read(input))
                .isEqualTo(new Markdown(
                        """
                    # Changelog

                    whatever

                    """,
                        List.of(
                                new Markdown.Section(
                                        "Unreleased",
                                        """

                        something

                        """),
                                new Markdown.Section(
                                        "v1",
                                        """

                        something else
                        """))));
    }
}
