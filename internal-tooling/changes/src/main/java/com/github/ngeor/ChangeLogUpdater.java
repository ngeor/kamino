package com.github.ngeor;

import com.github.ngeor.markdown.Markdown;
import com.github.ngeor.markdown.MarkdownReader;
import com.github.ngeor.markdown.MarkdownWriter;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ChangeLogUpdater {
    private final File rootDirectory;
    private final String modulePath;
    private final String tagPrefix;
    private final Git git;

    public ChangeLogUpdater(File rootDirectory, String modulePath) {
        this(rootDirectory, modulePath, new Git(rootDirectory));
    }

    public ChangeLogUpdater(File rootDirectory, String modulePath, Git git) {
        this(rootDirectory, modulePath, TagPrefix.tagPrefix(modulePath), git);
    }

    public ChangeLogUpdater(File rootDirectory, String modulePath, String tagPrefix, Git git) {
        this.rootDirectory = rootDirectory;
        this.modulePath = modulePath;
        this.tagPrefix = tagPrefix;
        this.git = git;
    }

    private File getChangeLog() {
        Path rootPath = rootDirectory.toPath();
        Path projectPath = modulePath == null ? rootPath : rootPath.resolve(modulePath);
        return projectPath.resolve("CHANGELOG.md").toFile();
    }

    public void updateChangeLog(String version) throws IOException, InterruptedException, ProcessFailedException {
        Markdown markdown = generateChangeLog(version);
        MarkdownWriter.write(markdown, getChangeLog());
    }

    private Markdown generateChangeLog(String version)
            throws IOException, ProcessFailedException, InterruptedException {
        String sinceCommit = version != null ? TagPrefix.tag(modulePath, SemVer.parse(version)) : null;

        FormattedRelease formattedRelease = format(
                Release.create(getEligibleCommits(sinceCommit))
                        .makeSubGroups(new Release.SubGroupOptions(
                                "chore",
                                List.of("feat", "fix", "chore", "deps"),
                                commitInfo -> "chore".equals(commitInfo.type()) && "deps".equals(commitInfo.scope())
                                        ? "deps"
                                        : commitInfo.type())),
                new FormatOptions(
                        tagPrefix,
                        "Unreleased",
                        Map.of(
                                "feat",
                                "Features",
                                "fix",
                                "Fixes",
                                "chore",
                                "Miscellaneous Tasks",
                                "deps",
                                "Dependencies")));

        File changeLog = getChangeLog();
        Markdown markdown = changeLog.isFile()
                ? MarkdownReader.read(changeLog)
                : new Markdown(String.format("# Changelog%n%n"), List.of());
        markdown = merge(markdown, formattedRelease);
        return markdown;
    }

    private Stream<Commit> getEligibleCommits(String sinceCommit)
            throws IOException, InterruptedException, ProcessFailedException {
        return git.revList(sinceCommit, modulePath).filter(c -> new CommitFilter().test(c.summary()));
    }

    private static FormattedRelease format(Release release, FormatOptions options) {
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
                subGroup.commits().stream().map(ChangeLogUpdater::formatCommit).toList());
    }

    private static String formatCommit(Release.CommitInfo commit) {
        if (commit.isBreaking()) {
            return String.format("**Breaking**: %s", commit.description());
        }

        return commit.description();
    }

    private Markdown merge(Markdown markdown, FormattedRelease formattedRelease) {
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
