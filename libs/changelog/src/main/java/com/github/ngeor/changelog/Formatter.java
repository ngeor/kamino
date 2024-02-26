package com.github.ngeor.changelog;

import com.github.ngeor.versions.SemVer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Formatter(FormatOptions options, String modulePath, SemVer futureVersion) {
    public FormattedRelease format(Release release) {
        Objects.requireNonNull(release);
        List<Release.Group> groups = release.groups();
        List<FormattedRelease.Group> formattedGroups = new ArrayList<>(groups.size());
        for (int i = 0; i < groups.size(); i++) {
            Release.Group group = groups.get(i);
            Release.Group olderGroup = i + 1 < groups.size() ? groups.get(i + 1) : null;
            FormattedRelease.Group formattedGroup = formatGroup(group, olderGroup);
            formattedGroups.add(formattedGroup);
        }
        return new FormattedRelease(formattedGroups);
    }

    private FormattedRelease.Group formatGroup(Release.Group group, Release.Group olderGroup) {
        final String currentTag;
        final String linkText;
        final LocalDate date;
        if (group instanceof Release.TaggedGroup taggedGroup) {
            // contains the full "libs/java/v4.1.0" text
            currentTag = taggedGroup.tag();
            // keep the "4.1.0" part of "libs/java/v4.1.0
            linkText = TagPrefix.forPath(modulePath).stripTagPrefixIfPresent(currentTag);
            date = taggedGroup.authorDate();
        } else if (futureVersion != null) {
            // the futureVersion does not have the prefix
            linkText = futureVersion.toString();
            // add the prefix back to the (non-existing) tag
            currentTag = TagPrefix.forPath(modulePath).addTagPrefix(futureVersion);
            // use current date when working on a "future" release
            date = LocalDate.now();
        } else {
            currentTag = null;
            linkText = null;
            date = null;
        }

        final String link;
        if (currentTag != null
                && olderGroup instanceof Release.TaggedGroup nextTaggedGroup
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

        return new FormattedRelease.Group(
                title, group.subGroups().stream().map(this::formatSubGroup).toList());
    }

    private FormattedRelease.SubGroup formatSubGroup(Release.SubGroup subGroup) {
        String title = options.subGroupNames().getOrDefault(subGroup.name(), subGroup.name());
        return new FormattedRelease.SubGroup(
                title, subGroup.commits().stream().map(this::formatCommit).toList());
    }

    private String formatCommit(Release.CommitInfo commit) {
        if (commit.isBreaking()) {
            return String.format("**Breaking**: %s", commit.description());
        }

        return commit.description();
    }
}
