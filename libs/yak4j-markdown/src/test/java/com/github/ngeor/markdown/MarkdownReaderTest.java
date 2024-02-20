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

                no problem
                """;

        assertThat(read(input))
                .isEqualTo(List.of(new Section(
                        1,
                        "Changelog",
                        new Line("whatever"),
                        new Section(2, "Unreleased", new Line("something")),
                        new Section(2, "v1", new Line("something else"), new Line(""), new Line("no problem")))));
    }

    @Test
    void testReadTextBeforeFirstHeadingTwoTopLevelHeaders() {
        String input =
                """
            oops

            # About us

            test

            # Overview
            """;
        assertThat(read(input))
                .isEqualTo(List.of(
                        new Line("oops"), new Section(1, "About us", new Line("test")), new Section(1, "Overview")));
    }

    @Test
    void testThreeLevelOutline() {
        String input =
                """
        # Changelog

        ## v2

        ### Bug Fixes

        * Some fix

        ### Features

        * Some feature

        ## v1

        ### Miscellaneous Tasks

        * Some task
        """;
        assertThat(read(input))
                .isEqualTo(List.of(new Section(
                        1,
                        "Changelog",
                        new Section(
                                2,
                                "v2",
                                new Section(3, "Bug Fixes", new Line("* Some fix")),
                                new Section(3, "Features", new Line("* Some feature"))),
                        new Section(2, "v1", new Section(3, "Miscellaneous Tasks", new Line("* Some task"))))));
    }

    private List<Item> read(String input) {
        return new MarkdownReader().read(input);
    }
}
