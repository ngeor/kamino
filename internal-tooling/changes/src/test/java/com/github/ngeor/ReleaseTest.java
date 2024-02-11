package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReleaseTest {

    @Test
    void testCreate() {
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

        // act
        Release release = Release.create(commits.stream());

        // assert
        assertThat(release.groups())
                .hasSize(2)
                .contains(new Release.Group(commits.subList(0, 2)), new Release.Group(commits.subList(2, 4)));
    }

    @Test
    void testFilter() {
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
        Release release = Release.create(commits.stream());

        // act
        release = release.filter(c -> !c.summary().contains("Initial"));

        // assert
        assertThat(release.groups())
                .hasSize(2)
                .contains(new Release.Group(commits.subList(0, 2)), new Release.Group(commits.subList(2, 3)));
    }

    @Test
    void testMakeSubGroups() {
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
        Release release = Release.create(commits.stream());

        // act
        release =
                release.makeSubGroups(new Release.SubGroupOptions("chore", List.of("release", "feat", "fix", "chore")));

        // assert
        assertThat(release.groups())
                .hasSize(2)
                .contains(
                        new Release.Group(
                                commits.get(0),
                                List.of(
                                        new Release.SubGroup("fix", new LinkedList<>(commits.subList(1, 2))),
                                        new Release.SubGroup("chore", new LinkedList<>(commits.subList(0, 1))))),
                        new Release.Group(
                                commits.get(2),
                                List.of(
                                        new Release.SubGroup("release", new LinkedList<>(commits.subList(2, 3))),
                                        new Release.SubGroup("chore", new LinkedList<>(commits.subList(3, 4))))));
    }
}
