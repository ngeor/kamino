package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 */
public final class App {
    App() {}

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // e.g. libs/java
        String path = args[0];
        // e.g. 4.2.1
        String version = args.length >= 2 ? args[1] : null;
        new App().run(path, version);
    }

    void run(String path, String version) throws IOException, InterruptedException {
        File rootDirectory = new File(".");

        String tagPrefix = path + "/v";
        String sinceCommit = version != null ? tagPrefix + version + "..HEAD" : null;

        Git git = new Git(rootDirectory);

        FormattedRelease formattedRelease = format(
                Release.create(git.revList(sinceCommit, path))
                        .filter(this::isRelevantToChangelog)
                        .makeSubGroups(new Release.SubGroupOptions("chore", List.of("feat", "fix"))),
                new FormatOptions(
                        tagPrefix,
                        "Unreleased",
                        Map.of("feat", "Features", "fix", "Fixes", "chore", "Miscellaneous Tasks")));

        File changeLog =
                rootDirectory.toPath().resolve(path).resolve("CHANGELOG.md").toFile();
        Markdown markdown = changeLog.isFile() ? MarkdownReader.read(changeLog) : new Markdown("# Changelog", List.of());
        markdown = merge(markdown, formattedRelease);
        MarkdownWriter.write(markdown, changeLog);
    }

    boolean isRelevantToChangelog(Commit commit) {
        List<String> needles = List.of("maven-release-plugin", "changelog");
        return needles.stream().noneMatch(needle -> commit.summary().contains(needle));
    }

    FormattedRelease format(Release release, FormatOptions options) {
        return new FormattedRelease(
                release.groups().stream().map(g -> format(g, options)).toList());
    }

    private FormattedRelease.Group format(Release.Group group, FormatOptions options) {
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

    private FormattedRelease.SubGroup format(Release.SubGroup subGroup, FormatOptions options) {
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
