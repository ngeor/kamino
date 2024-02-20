package com.github.ngeor.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
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
        List<Item> markdown = new MarkdownReader().read(input);
        String output = new MarkdownWriter().write(markdown);
        assertThat(output).isEqualToNormalizingNewlines(input);
    }
}
