package com.github.ngeor.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MarkdownReaderTest {
    @Test
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

        String header = """
        # Changelog

        whatever

        """;
        String firstSectionBody = """

        something

        """;
        String secondSectionBody = """

        something else
        """;
        assertThat(MarkdownReader.read(input))
                .isEqualTo(new Markdown(
                        header,
                        List.of(
                                new Markdown.Section("Unreleased", firstSectionBody),
                                new Markdown.Section("v1", secondSectionBody))));
    }
}
