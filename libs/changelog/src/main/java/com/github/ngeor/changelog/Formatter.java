package com.github.ngeor.changelog;

import java.time.LocalDate;

public record Formatter(FormatOptions options, String modulePath) {
    public FormattedRelease format(Release release) {
        return new FormattedRelease(
                release.groups().stream().map(this::formatGroup).toList());
    }

    private FormattedRelease.Group formatGroup(Release.Group group) {
        final String title;
        if (group instanceof Release.TaggedGroup taggedGroup) {
            title = String.format(
                    "[%s] - %s",
                    TagPrefix.forPath(modulePath).stripTagPrefixIfPresent(taggedGroup.tag()), taggedGroup.authorDate());
        } else if (options.futureVersion() != null) {
            // use current date when working on a "future" release
            title = String.format("[%s] - %s", options.futureVersion(), LocalDate.now());
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
