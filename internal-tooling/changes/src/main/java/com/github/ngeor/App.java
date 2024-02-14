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
 * Changelog and semantic version calculator.
 */
public final class App {
    private final File rootDirectory;
    private final String path;
    private final String tagPrefix;
    private final Git git;

    App(String path) {
        this.rootDirectory = new File(".").toPath().toAbsolutePath().toFile();

        // ensure given path exists
        if (!new File(rootDirectory, path).isDirectory()) {
            throw new IllegalArgumentException("path " + path + " not found");
        }

        this.path = path;
        this.tagPrefix = path + "/v";
        this.git = new Git(rootDirectory);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", true);
        parser.addPositionalArgument("version", false);
        parser.addFlagArgument("git-version");
        Map<String, Object> parsedArgs = parser.parse(args);
        // e.g. libs/java
        // ensure path does not end in slashes and is not blank
        String path = sanitize((String) parsedArgs.get("path"));
        // e.g. 4.2.1
        String version = (String) parsedArgs.get("version");
        App app = new App(path);
        if (parsedArgs.containsKey("git-version")) {
            app.calculateGitVersion();
        } else {
            app.updateChangeLog(version);
        }
    }

    void updateChangeLog(String version) throws IOException, InterruptedException {
        String sinceCommit = version != null ? tagPrefix + version + "..HEAD" : null;

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
        Markdown markdown =
                changeLog.isFile() ? MarkdownReader.read(changeLog) : new Markdown("# Changelog", List.of());
        markdown = merge(markdown, formattedRelease);
        MarkdownWriter.write(markdown, changeLog);
    }

    private void calculateGitVersion() throws IOException, InterruptedException {
        SemVer tag = SemVer.parse(git.getMostRecentTag(tagPrefix));
        String sinceCommit = tagPrefix + tag + "..HEAD";

        List<Commit> commits = git.revList(sinceCommit, path).toList();
        if (commits.isEmpty()) {
            System.out.printf("No commits to %s since %s%n", path, tag);
            return;
        }

        SemVerBump bump = commits.stream()
                .filter(this::isRelevantToChangelog)
                .map(Commit::summary)
                .map(this::calculateBump)
                .max(Enum::compareTo)
                .orElse(SemVerBump.MINOR);

        SemVer nextTag = tag.bump(bump);
        System.out.printf("The next version of %s should be %s (%s)%n", path, nextTag, bump);
    }

    private static String sanitize(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path is mandatory");
        }

        // ensure path does not end in slash
        while (path.endsWith("\\") || path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (path.isBlank()) {
            throw new IllegalArgumentException("path is mandatory");
        }

        return path;
    }

    SemVerBump calculateBump(String message) {
        if (message.startsWith("fix:")) {
            return SemVerBump.PATCH;
        }

        if (message.matches("^[a-z]+!:.+$")) {
            return SemVerBump.MAJOR;
        }

        return SemVerBump.MINOR;
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
