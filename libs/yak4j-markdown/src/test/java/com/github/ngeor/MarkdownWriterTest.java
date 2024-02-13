package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class MarkdownWriterTest {
    @Test
    void testWrite() throws IOException {
        String input =
                """
            # Changelog

            whatever

            ## Unreleased

            something

            ## v1

            something else
            """;
        Markdown markdown = MarkdownReader.read(input, "\n");
        String output = MarkdownWriter.write(markdown, "\n");
        assertThat(output).isEqualTo(input);
    }
}
