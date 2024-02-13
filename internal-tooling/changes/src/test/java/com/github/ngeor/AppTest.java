package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    @Test
    void testFormat() {
        // arrange
        String input =
                """
        sha|2024-02-11||chore: Whatever
        sha|2024-02-10||fix: Something
        sha|2024-02-09|tag: v1.0|release: New version
        sha|2024-02-08||Initial commit
        """;

        List<Commit> commits =
                input.lines().map(line -> Commit.parse(line).orElseThrow()).toList();
        App app = new App();
        Release release = Release.create(commits.stream())
                .makeSubGroups(new Release.SubGroupOptions("chore", List.of("fix", "chore", "release")));

        // act
        FormattedRelease result = app.format(
                release,
                new FormatOptions(
                        "v",
                        "Unreleased",
                        Map.of("fix", "Fixes", "chore", "Miscellaneous Tasks", "release", "Release")));

        // assert
        assertThat(result.groups())
                .hasSize(2)
                .contains(
                        new FormattedRelease.Group(
                                "Unreleased",
                                List.of(
                                        new FormattedRelease.SubGroup("Fixes", List.of("Something")),
                                        new FormattedRelease.SubGroup("Miscellaneous Tasks", List.of("Whatever")))),
                        new FormattedRelease.Group(
                                "[1.0] - 2024-02-09",
                                List.of(
                                        new FormattedRelease.SubGroup("Miscellaneous Tasks", List.of("Initial commit")),
                                        new FormattedRelease.SubGroup("Release", List.of("New version")))));
    }
}
