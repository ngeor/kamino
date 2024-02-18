package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeLogUpdater {
    private final File rootDirectory;
    private final String path;
    private final String tagPrefix;
    private final Git git;

    public ChangeLogUpdater(File rootDirectory, String path) {
        this.rootDirectory = rootDirectory;
        this.path = path;
        this.tagPrefix = TagPrefix.tagPrefix(path);
        this.git = new Git(rootDirectory);
    }

    public void updateChangeLog(String version) throws IOException, InterruptedException, ProcessFailedException {
        String sinceCommit = version != null ? TagPrefix.tag(path, SemVer.parse(version)) : null;

        FormattedRelease formattedRelease = format(
                Release.create(git.revList(sinceCommit, path))
                        .filter(new CommitFilter())
                        .makeSubGroups(new Release.SubGroupOptions("chore", List.of("feat", "fix"))),
                new FormatOptions(
                        tagPrefix,
                        "Unreleased",
                        Map.of("feat", "Features", "fix", "Fixes", "chore", "Miscellaneous Tasks")));

        File changeLog =
                rootDirectory.toPath().resolve(path).resolve("CHANGELOG.md").toFile();
        Markdown markdown =
                changeLog.isFile() ? MarkdownReader.read(changeLog) : new Markdown("# Changelog", List.of());
        markdown = merge(markdown, formattedRelease);
        MarkdownWriter.write(markdown, changeLog);
    }

    static FormattedRelease format(Release release, FormatOptions options) {
        return new FormattedRelease(
                release.groups().stream().map(g -> format(g, options)).toList());
    }

    private static FormattedRelease.Group format(Release.Group group, FormatOptions options) {
        String title = group.tag().tag();
        if (title != null) {
            if (title.startsWith(options.tagPrefix())) {
                title = title.substring(options.tagPrefix().length());
            }
            title = "[" + title + "] - " + group.tag().authorDate();
        } else {
            title = options.defaultTag();
        }

        return new FormattedRelease.Group(
                title, group.subGroups().stream().map(g -> format(g, options)).toList());
    }

    private static FormattedRelease.SubGroup format(Release.SubGroup subGroup, FormatOptions options) {
        String title = options.subGroupNames().getOrDefault(subGroup.name(), subGroup.name());
        return new FormattedRelease.SubGroup(
                title,
                subGroup.commits().stream()
                        .map(Commit::summary)
                        .map(s -> Arrays.asList(s.split(":", 2))
                                .reversed()
                                .getFirst()
                                .trim())
                        .toList());
    }

    Markdown merge(Markdown markdown, FormattedRelease formattedRelease) {
        List<Markdown.Section> sections = new ArrayList<>();
        Set<String> seenTitles = new HashSet<>();
        for (var it = formattedRelease.groups().iterator(); it.hasNext(); ) {
            var formattedGroup = it.next();
            StringBuilder body = new StringBuilder();
            body.append(System.lineSeparator());
            for (var itChild = formattedGroup.subGroups().iterator(); itChild.hasNext(); ) {
                var childGroup = itChild.next();
                body.append(String.format("### %s%n%n", childGroup.title()));
                for (String item : childGroup.items()) {
                    body.append(String.format("* %s%n", item));
                }
                if (itChild.hasNext()) {
                    body.append(System.lineSeparator());
                }
            }
            if (it.hasNext() || markdown.sections().stream().anyMatch(s -> !seenTitles.contains(s.title()))) {
                body.append(System.lineSeparator());
            }
            sections.add(new Markdown.Section(formattedGroup.title(), body.toString()));
            seenTitles.add(formattedGroup.title());
        }
        sections.addAll(markdown.sections().stream()
                .filter(s -> !seenTitles.contains(s.title()))
                .toList());

        return new Markdown(markdown.header(), sections);
    }
}
