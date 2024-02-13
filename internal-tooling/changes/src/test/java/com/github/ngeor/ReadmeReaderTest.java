package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReadmeReaderTest {
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

        assertThat(ReadmeReader.read(input, "\n"))
                .isEqualTo(new Readme(
                        """
                    # Changelog

                    whatever

                    """,
                        List.of(
                                new Readme.Section(
                                        "Unreleased",
                                        """

                        something

                        """),
                                new Readme.Section(
                                        "v1",
                                        """

                        something else
                        """))));
    }
}
