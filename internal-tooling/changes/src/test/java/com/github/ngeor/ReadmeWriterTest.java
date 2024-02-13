package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ReadmeWriterTest {
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
        Readme readme = ReadmeReader.read(input, "\n");
        String output = ReadmeWriter.write(readme, "\n");
        assertThat(output).isEqualTo(input);
    }
}
