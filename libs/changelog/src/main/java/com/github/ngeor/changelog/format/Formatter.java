package com.github.ngeor.changelog.format;

import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.changelog.group.CommitInfo;
import com.github.ngeor.changelog.group.Group;
import com.github.ngeor.changelog.group.Release;
import com.github.ngeor.changelog.group.SubGroup;
import com.github.ngeor.changelog.group.TaggedGroup;
import com.github.ngeor.versions.SemVer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Formatter(FormatOptions options, TagPrefix tagPrefix, SemVer futureVersion) {
    public static FormattedRelease format(Release release, FormatOptions options, String modulePath, SemVer futureVersion) {
        return new Formatter(options, TagPrefix.forPath(modulePath), futureVersion).format(release);
    }

    public static FormattedRelease format(Release release, FormatOptions options, String modulePath) {
        return format(release, options, modulePath, null);
    }

    public FormattedRelease format(Release release) {
        Objects.requireNonNull(release);
        List<Group> groups = release.groups();
        List<FormattedGroup> formattedGroups = new ArrayList<>(groups.size());
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            Group olderGroup = i + 1 < groups.size() ? groups.get(i + 1) : null;
            FormattedGroup formattedGroup = formatGroup(group, olderGroup);
            formattedGroups.add(formattedGroup);
        }
        return new FormattedRelease(formattedGroups);
    }

    private FormattedGroup formatGroup(Group group, Group olderGroup) {
        final String currentTag;
        final String linkText;
        final LocalDate date;
        if (group instanceof TaggedGroup taggedGroup) {
            // contains the full "libs/java/v4.1.0" text
            currentTag = taggedGroup.tag();
            // keep the "4.1.0" part of "libs/java/v4.1.0
            linkText = tagPrefix.stripTagPrefixIfPresent(currentTag);
            date = taggedGroup.authorDate();
        } else if (futureVersion != null) {
            // the futureVersion does not have the prefix
            linkText = futureVersion.toString();
            // add the prefix back to the (non-existing) tag
            currentTag = tagPrefix.addTagPrefix(futureVersion);
            // use current date when working on a "future" release
            date = LocalDate.now();
        } else {
            currentTag = null;
            linkText = null;
            date = null;
        }

        final String link;
        if (currentTag != null
                && olderGroup instanceof TaggedGroup nextTaggedGroup
                && options.compareUrlTemplate() != null) {
            link = String.format(options.compareUrlTemplate(), nextTaggedGroup.tag(), currentTag);
        } else {
            link = null;
        }

        final String title;
        if (link != null) {
            title = String.format("[%s](%s) - %s", linkText, link, date);
        } else if (currentTag != null) {
            title = String.format("[%s] - %s", linkText, date);
        } else {
            title = options.unreleasedTitle();
        }

        return new FormattedGroup(
                title, group.subGroups().stream().map(this::formatSubGroup).toList());
    }

    private FormattedSubGroup formatSubGroup(SubGroup subGroup) {
        String title = options.subGroupNames().getOrDefault(subGroup.name(), subGroup.name());
        return new FormattedSubGroup(
                title, subGroup.commits().stream().map(this::formatCommit).toList());
    }

    private String formatCommit(CommitInfo commit) {
        if (commit.isBreaking()) {
            return String.format("**Breaking**: %s", commit.description());
        }

        return commit.description();
    }
}
