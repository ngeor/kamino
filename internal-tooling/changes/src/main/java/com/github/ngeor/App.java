package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public static void main(String[] args) throws IOException, InterruptedException, ProcessFailedException {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", false);
        parser.addPositionalArgument("version", false);
        parser.addFlagArgument("git-version");
        parser.addFlagArgument("release");
        parser.addFlagArgument("dry-run");
        parser.addFlagArgument("push");
        Map<String, Object> parsedArgs = parser.parse(args);
        // e.g. libs/java
        // ensure path does not end in slashes and is not blank
        String path = sanitize((String) parsedArgs.get("path"));
        // e.g. 4.2.1
        String version = (String) parsedArgs.get("version");
        if (path != null) {
            App app = new App(path);
            if (parsedArgs.containsKey("git-version")) {
                app.calculateGitVersion();
            } else if (parsedArgs.containsKey("release")) {
                app.release(parsedArgs.containsKey("dry-run"), parsedArgs.containsKey("push"));
            } else {
                app.updateChangeLog(version);
            }
        } else {
            new ChangesOverviewCommand().run();
        }
    }

    private void updateChangeLog(String version) throws IOException, InterruptedException, ProcessFailedException {
        String sinceCommit = version != null ? tagPrefix + version : null;

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

    private SemVer calculateGitVersion() throws IOException, InterruptedException, ProcessFailedException {
        SemVer mostRecentVersion = SemVer.parse(git.getMostRecentTag(tagPrefix).orElseThrow());
        String sinceCommit = tagPrefix + mostRecentVersion;

        List<Commit> commits = git.revList(sinceCommit, path).toList();
        if (commits.isEmpty()) {
            System.out.printf("No commits to %s since %s%n", path, mostRecentVersion);
            return null;
        }

        SemVerBump bump = commits.stream()
                .filter(new CommitFilter())
                .map(Commit::summary)
                .map(this::calculateBump)
                .max(Enum::compareTo)
                .orElse(SemVerBump.MINOR);

        SemVer nextVersion = mostRecentVersion.bump(bump);
        System.out.printf("The next version of %s should be %s (%s)%n", path, nextVersion, bump);
        return nextVersion;
    }

    private void release(boolean dryRun, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {
        SemVer nextVersion = Objects.requireNonNull(calculateGitVersion());
        MavenReleaser.prepareRelease(rootDirectory, path, nextVersion, dryRun, push);
    }

    static String sanitize(String path) {
        if (path == null) {
            return null;
        }

        // ensure path does not end in slash
        while (path.endsWith("\\") || path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path.isBlank() ? null : path;
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
