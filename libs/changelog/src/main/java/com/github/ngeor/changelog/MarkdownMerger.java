package com.github.ngeor.changelog;

import com.github.ngeor.changelog.format.FormatOptions;
import com.github.ngeor.changelog.format.FormattedGroup;
import com.github.ngeor.changelog.format.FormattedRelease;
import com.github.ngeor.markdown.Item;
import com.github.ngeor.markdown.Line;
import com.github.ngeor.markdown.Section;
import com.github.ngeor.versions.SemVer;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MarkdownMerger(FormatOptions formatOptions, boolean overwrite) {
    // TODO make the pattern configurable, aligning with the formatter,
    // but offering more flexibility, in case past changelog entries were generated in a different format
    private static final Pattern semVer = Pattern.compile("^\\[(\\d+)\\.(\\d+)\\.(\\d+)].*$");

    public void mergeIntoLeft(List<Item> markdown, FormattedRelease formattedRelease) {
        Objects.requireNonNull(markdown);
        Objects.requireNonNull(formattedRelease);

        // find the H1 section in the existing markdown
        Section topLevelSection = findTopLevelSection(markdown);
        int i = 0;
        int j = 0;
        while (i < topLevelSection.contents().size()
                && j < formattedRelease.groups().size()) {
            Item leftItem = topLevelSection.contents().get(i);
            if (leftItem instanceof Section leftSection && leftSection.level() == 2) {
                FormattedGroup rightGroup = formattedRelease.groups().get(j);
                String leftTitle = leftSection.title();
                String rightTitle = rightGroup.title();
                if (formatOptions.unreleasedTitle().equalsIgnoreCase(leftTitle)) {
                    // left side in unreleased
                    if (formatOptions.unreleasedTitle().equalsIgnoreCase(rightTitle)) {
                        // right side is also unreleased
                        // always overwrite unreleased section, right wins
                        topLevelSection.contents().set(i, generateSection(rightGroup));
                        i++;
                        j++;
                    } else {
                        // left is unreleased, right is not
                        // this means we don't have any more unreleased changes
                        topLevelSection.contents().remove(i);
                    }
                } else {
                    // left side is not unreleased
                    if (formatOptions.unreleasedTitle().equalsIgnoreCase(rightTitle)) {
                        // insert right item
                        topLevelSection.contents().add(i, generateSection(rightGroup));
                        i++;
                        j++;
                    } else {
                        // compare titles, keep overwrite into account
                        // most recent release comes first
                        int cmp = compareTitle(leftTitle, rightTitle);
                        if (cmp == 0) {
                            if (overwrite) {
                                topLevelSection.contents().set(i, generateSection(rightGroup));
                            }
                            i++;
                            j++;
                        } else if (cmp < 0) {
                            // left side is smaller
                            // since bigger wins, insert right first
                            topLevelSection.contents().add(i, generateSection(rightGroup));
                            i++;
                            j++;
                        } else {
                            // left side is bigger, let it be
                            i++;
                        }
                    }
                }
            } else {
                // skip over left side thing that isn't a H2
                i++;
            }
        }

        while (j < formattedRelease.groups().size()) {
            topLevelSection
                    .contents()
                    .add(generateSection(formattedRelease.groups().get(j)));
            j++;
        }
    }

    private Section generateSection(FormattedGroup group) {
        String title = group.title();
        return new Section(2, title, formatSectionBody(group));
    }

    private Section findTopLevelSection(List<Item> markdown) {
        return markdown.stream()
                .filter(Section.class::isInstance)
                .map(Section.class::cast)
                .filter(s -> s.level() == 1)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Changelog must have a top level heading"));
    }

    private List<Item> formatSectionBody(FormattedGroup formattedGroup) {
        return formattedGroup.subGroups().stream()
                .map(childGroup -> {
                    List<Item> body = childGroup.items().stream()
                            .map(item -> String.format("* %s", item))
                            .map(Line::new)
                            .map(Item.class::cast)
                            .toList();
                    return new Section(3, childGroup.title(), body);
                })
                .map(Item.class::cast)
                .toList();
    }

    private int compareTitle(String left, String right) {
        Matcher leftMatcher = semVer.matcher(left);
        Matcher rightMatcher = semVer.matcher(right);
        if (leftMatcher.matches() && rightMatcher.matches()) {
            SemVer leftVersion = fromMatcher(leftMatcher);
            SemVer rightVersion = fromMatcher(rightMatcher);
            return leftVersion.compareTo(rightVersion);
        }
        return left.compareToIgnoreCase(right);
    }

    private SemVer fromMatcher(Matcher matcher) {
        return new SemVer(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)));
    }
}
