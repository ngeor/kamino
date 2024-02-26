package com.github.ngeor.changelog.format;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.changelog.group.ConventionalCommit;
import com.github.ngeor.changelog.group.Release;
import com.github.ngeor.changelog.group.SubGroup;
import com.github.ngeor.changelog.group.TaggedGroup;
import com.github.ngeor.changelog.group.UnreleasedGroup;
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
        Release release = new Release(new UnreleasedGroup(
                new SubGroup("chore", new ConventionalCommit("chore", null, "Added tests", false))));

        // act
        FormattedRelease formatted = Formatter.format(release, options, moduleName);

        // assert
        assertThat(formatted).isNotNull();
        assertThat(formatted.groups()).hasSize(1);
        FormattedGroup group = formatted.groups().get(0);
        assertThat(group.title()).isEqualTo("Unreleased");
        assertThat(group.subGroups()).hasSize(1);
        FormattedSubGroup subGroup = group.subGroups().get(0);
        assertThat(subGroup.title()).isEqualTo("Chores");
        assertThat(subGroup.items()).containsExactly("Added tests");
    }

    @Test
    void testBreakingChangeCommit() {
        // arrange
        FormatOptions options = new FormatOptions("Unreleased", Map.of("chore", "Chores"), null);
        String moduleName = "libs/java";
        Release release = new Release(
                new UnreleasedGroup(new SubGroup("chore", new ConventionalCommit("chore", null, "Added tests", true))));

        // act
        FormattedRelease formatted = Formatter.format(release, options, moduleName);

        // assert
        assertThat(formatted).isNotNull();
        FormattedGroup group = formatted.groups().get(0);
        FormattedSubGroup subGroup = group.subGroups().get(0);
        assertThat(subGroup.items()).containsExactly("**Breaking**: Added tests");
    }

    @Test
    void testReleaseTitle() {
        // arrange
        FormatOptions options = new FormatOptions("Unreleased", Map.of("chore", "Chores"), null);
        String moduleName = "libs/java";
        Release release = new Release(new TaggedGroup(
                new Commit("sha", LocalDate.of(2024, 2, 26), "libs/java/v1.0.0", "Release v1.0.0"),
                new SubGroup("chore", new ConventionalCommit("chore", null, "Added tests", true))));

        // act
        FormattedRelease formatted = Formatter.format(release, options, moduleName);

        // assert
        assertThat(formatted).isNotNull();
        assertThat(formatted.groups()).hasSize(1);
        FormattedGroup group = formatted.groups().get(0);
        assertThat(group.title()).isEqualTo("[1.0.0] - 2024-02-26");
    }

    @Test
    void testReleaseTitlePointsToDiff() {
        // arrange
        FormatOptions options = new FormatOptions(
                "Unreleased", Map.of("chore", "Chores"), "https://github.com/ngeor/changelog/compare/%s...%s");
        String moduleName = "libs/java";
        Release release = new Release(
                new TaggedGroup(
                        new Commit("sha", LocalDate.of(2024, 2, 27), "libs/java/v1.1.0", "Release v1.1.0"),
                        new SubGroup("chore", new ConventionalCommit("chore", null, "Fixed tests", false))),
                new TaggedGroup(
                        new Commit("sha", LocalDate.of(2024, 2, 26), "libs/java/v1.0.0", "Release v1.0.0"),
                        new SubGroup("chore", new ConventionalCommit("chore", null, "Added tests", true))));

        // act
        FormattedRelease formatted = Formatter.format(release, options, moduleName);

        // assert
        assertThat(formatted).isNotNull();
        assertThat(formatted.groups()).hasSize(2);
        assertThat(formatted.groups().get(0).title())
                .isEqualTo(
                        "[1.1.0](https://github.com/ngeor/changelog/compare/libs/java/v1.0.0...libs/java/v1.1.0) - 2024-02-27");
        assertThat(formatted.groups().get(1).title()).isEqualTo("[1.0.0] - 2024-02-26");
    }
}
