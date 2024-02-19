package com.github.ngeor.markdown;

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
        Markdown markdown = MarkdownReader.read(input);
        String output = MarkdownWriter.write(markdown);
        assertThat(output).isEqualToNormalizingNewlines(input);
    }
}
