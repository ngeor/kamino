package com.github.ngeor.changelog;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.markdown.Item;
import com.github.ngeor.markdown.Line;
import com.github.ngeor.markdown.Section;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MarkdownMergerTest {

    private static final String UNRELEASED = "Unreleased";
    private static final String CHANGELOG = "Changelog";
    private static final String CHORES = "Chores";
    private static final String FIXES = "Fixes";

    @Test
    void test() {
        // arrange
        MarkdownMerger markdownMerger = new MarkdownMerger(new FormatOptions(UNRELEASED, Map.of(), null), false);
        List<Item> markdown = List.of(new Section(1, CHANGELOG));
        FormattedRelease formattedRelease = new FormattedRelease(
                new FormattedRelease.Group(UNRELEASED, new FormattedRelease.SubGroup(CHORES, "Simple fix")));

        // act
        markdownMerger.mergeIntoLeft(markdown, formattedRelease);

        // assert
        assertThat(markdown)
                .containsExactly(new Section(
                        1, CHANGELOG, new Section(2, UNRELEASED, new Section(3, CHORES, new Line("* Simple fix")))));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testDeleteExistingUnreleasedSectionIfEverythingIsReleased(boolean overwrite) {
        // arrange
        MarkdownMerger markdownMerger = new MarkdownMerger(new FormatOptions(UNRELEASED, Map.of(), null), overwrite);
        List<Item> markdown = List.of(new Section(
                1, CHANGELOG, new Section(2, UNRELEASED, new Section(3, CHORES, new Line("* Simple fix")))));
        FormattedRelease formattedRelease = new FormattedRelease(
                new FormattedRelease.Group("[1.0]", new FormattedRelease.SubGroup(CHORES, "Simple fix")));

        // act
        markdownMerger.mergeIntoLeft(markdown, formattedRelease);

        // assert
        assertThat(markdown)
                .containsExactly(new Section(
                        1, CHANGELOG, new Section(2, "[1.0]", new Section(3, CHORES, new Line("* Simple fix")))));
    }

    @Test
    void testOverwriteExistingUnreleased() {
        // arrange
        MarkdownMerger markdownMerger = new MarkdownMerger(new FormatOptions(UNRELEASED, Map.of(), null), false);
        List<Item> markdown = List.of(new Section(
                1, CHANGELOG, new Section(2, UNRELEASED, new Section(3, CHORES, new Line("* Simple fix")))));
        FormattedRelease formattedRelease = new FormattedRelease(
                new FormattedRelease.Group(UNRELEASED, new FormattedRelease.SubGroup(FIXES, "Another simple fix")),
                new FormattedRelease.Group("[1.0]", new FormattedRelease.SubGroup(CHORES, "Simple fix")));

        // act
        markdownMerger.mergeIntoLeft(markdown, formattedRelease);

        // assert
        assertThat(markdown)
                .containsExactly(new Section(
                        1,
                        CHANGELOG,
                        new Section(2, UNRELEASED, new Section(3, FIXES, new Line("* Another simple fix"))),
                        new Section(2, "[1.0]", new Section(3, CHORES, new Line("* Simple fix")))));
    }

    @Test
    void testOverwriteDetectsSemVer() {
        // arrange
        MarkdownMerger markdownMerger = new MarkdownMerger(new FormatOptions(UNRELEASED, Map.of(), null), true);
        List<Item> markdown = List.of(new Section(
                1,
                CHANGELOG,
                new Section(2, "[1.3.0]", new Section(3, CHORES, new Line("* Help"))),
                new Section(2, "[1.2.1] whatever", new Section(3, CHORES, new Line("* Gone")))));

        FormattedRelease formattedRelease = new FormattedRelease(
                new FormattedRelease.Group("[1.4.0]", new FormattedRelease.SubGroup(FIXES, "Another simple fix")),
                new FormattedRelease.Group("[1.2.1]", new FormattedRelease.SubGroup(CHORES, "Simple fix")));

        // act
        markdownMerger.mergeIntoLeft(markdown, formattedRelease);

        // assert
        assertThat(markdown)
                .containsExactly(new Section(
                        1,
                        CHANGELOG,
                        new Section(2, "[1.4.0]", new Section(3, FIXES, new Line("* Another simple fix"))),
                        new Section(2, "[1.3.0]", new Section(3, CHORES, new Line("* Help"))),
                        new Section(2, "[1.2.1]", new Section(3, CHORES, new Line("* Simple fix")))));
    }
}
