package com.github.ngeor.changelog;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.git.Commit;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FormatterTest {
    @Test
    void test() {
        // arrange
        FormatOptions options = new FormatOptions("Unreleased", Map.of("chore", "Chores"), null);
        String moduleName = "libs/java";
        Release release = new Release(new Release.UnreleasedGroup(new Release.SubGroup(
                "chore", new Release.CommitInfo.ConventionalCommit("chore", null, "Added tests", false))));

        // act
        FormattedRelease formatted = new Formatter(options, moduleName, null).format(release);

        // assert
        assertThat(formatted).isNotNull();
        assertThat(formatted.groups()).hasSize(1);
        FormattedRelease.Group group = formatted.groups().get(0);
        assertThat(group.title()).isEqualTo("Unreleased");
        assertThat(group.subGroups()).hasSize(1);
        FormattedRelease.SubGroup subGroup = group.subGroups().get(0);
        assertThat(subGroup.title()).isEqualTo("Chores");
        assertThat(subGroup.items()).containsExactly("Added tests");
    }

    @Test
    void testBreakingChangeCommit() {
        // arrange
        FormatOptions options = new FormatOptions("Unreleased", Map.of("chore", "Chores"), null);
        String moduleName = "libs/java";
        Release release = new Release(new Release.UnreleasedGroup(new Release.SubGroup(
                "chore", new Release.CommitInfo.ConventionalCommit("chore", null, "Added tests", true))));

        // act
        FormattedRelease formatted = new Formatter(options, moduleName, null).format(release);

        // assert
        assertThat(formatted).isNotNull();
        FormattedRelease.Group group = formatted.groups().get(0);
        FormattedRelease.SubGroup subGroup = group.subGroups().get(0);
        assertThat(subGroup.items()).containsExactly("**Breaking**: Added tests");
    }

    @Test
    void testReleaseTitle() {
        // arrange
        FormatOptions options = new FormatOptions("Unreleased", Map.of("chore", "Chores"), null);
        String moduleName = "libs/java";
        Release release = new Release(new Release.TaggedGroup(
                new Commit("sha", LocalDate.of(2024, 2, 26), "libs/java/v1.0.0", "Release v1.0.0"),
                new Release.SubGroup(
                        "chore", new Release.CommitInfo.ConventionalCommit("chore", null, "Added tests", true))));

        // act
        FormattedRelease formatted = new Formatter(options, moduleName, null).format(release);

        // assert
        assertThat(formatted).isNotNull();
        assertThat(formatted.groups()).hasSize(1);
        FormattedRelease.Group group = formatted.groups().get(0);
        assertThat(group.title()).isEqualTo("[1.0.0] - 2024-02-26");
    }

    @Test
    void testReleaseTitlePointsToDiff() {
        // arrange
        FormatOptions options = new FormatOptions(
                "Unreleased", Map.of("chore", "Chores"), "https://github.com/ngeor/changelog/compare/%s...%s");
        String moduleName = "libs/java";
        Release release = new Release(
                new Release.TaggedGroup(
                        new Commit("sha", LocalDate.of(2024, 2, 27), "libs/java/v1.1.0", "Release v1.1.0"),
                        new Release.SubGroup(
                                "chore",
                                new Release.CommitInfo.ConventionalCommit("chore", null, "Fixed tests", false))),
                new Release.TaggedGroup(
                        new Commit("sha", LocalDate.of(2024, 2, 26), "libs/java/v1.0.0", "Release v1.0.0"),
                        new Release.SubGroup(
                                "chore",
                                new Release.CommitInfo.ConventionalCommit("chore", null, "Added tests", true))));

        // act
        FormattedRelease formatted = new Formatter(options, moduleName, null).format(release);

        // assert
        assertThat(formatted).isNotNull();
        assertThat(formatted.groups()).hasSize(2);
        assertThat(formatted.groups().get(0).title())
                .isEqualTo(
                        "[1.1.0](https://github.com/ngeor/changelog/compare/libs/java/v1.0.0...libs/java/v1.1.0) - 2024-02-27");
        assertThat(formatted.groups().get(1).title()).isEqualTo("[1.0.0] - 2024-02-26");
    }
}
