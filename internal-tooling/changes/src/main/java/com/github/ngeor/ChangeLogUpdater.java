package com.github.ngeor;

import com.github.ngeor.markdown.Item;
import com.github.ngeor.markdown.Line;
import com.github.ngeor.markdown.MarkdownReader;
import com.github.ngeor.markdown.MarkdownWriter;
import com.github.ngeor.markdown.Section;
import com.github.ngeor.mr.TagPrefix;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangeLogUpdater {
    private final File rootDirectory;
    private final String modulePath;
    private final Git git;

    public ChangeLogUpdater(File rootDirectory, String modulePath) {
        this.rootDirectory = rootDirectory;
        this.modulePath = modulePath;
        this.git = new Git(rootDirectory);
    }

    private File getChangeLog() {
        Path rootPath = rootDirectory.toPath();
        Path projectPath = modulePath == null ? rootPath : rootPath.resolve(modulePath);
        return projectPath.resolve("CHANGELOG.md").toFile();
    }

    void updateChangeLog() throws IOException, InterruptedException, ProcessFailedException {
        updateChangeLog(false);
    }

    public void updateChangeLog(boolean overwrite) throws IOException, InterruptedException, ProcessFailedException {
        List<Item> markdown = generateChangeLog(overwrite);
        new MarkdownWriter().write(markdown, getChangeLog());
    }

    private List<Item> generateChangeLog(boolean overwrite)
            throws IOException, ProcessFailedException, InterruptedException {

        Release.SubGroupOptions subGroupOptions =
                new Release.SubGroupOptions("chore", List.of("feat", "fix", "chore", "deps"), this::remapType);
        FormatOptions formatOptions = new FormatOptions(
                "Unreleased",
                Map.of("feat", "Features", "fix", "Fixes", "chore", "Miscellaneous Tasks", "deps", "Dependencies"));

        ReleaseGrouper releaseGrouper = new ReleaseGrouper(subGroupOptions);
        Release release = releaseGrouper.toRelease(getCommits());
        FormattedRelease formattedRelease = format(release, formatOptions);

        File changeLog = getChangeLog();
        List<Item> markdown = changeLog.isFile()
                ? new MarkdownReader().read(changeLog)
                : new ArrayList<>(List.of(new Section(1, "Changelog")));
        merge(markdown, formattedRelease, formatOptions, overwrite);
        return markdown;
    }

    private String remapType(Release.CommitInfo commitInfo) {
        return "chore".equals(commitInfo.type()) && "deps".equals(commitInfo.scope())
                ? "deps"
                : ("refactor".equals(commitInfo.type()) ? "chore" : commitInfo.type());
    }

    private Stream<Commit> getCommits() throws IOException, InterruptedException, ProcessFailedException {
        return git.revList((String) null, modulePath);
    }

    private FormattedRelease format(Release release, FormatOptions options) {
        return new FormattedRelease(
                release.groups().stream().map(g -> format(g, options)).toList());
    }

    private FormattedRelease.Group format(Release.Group group, FormatOptions options) {
        String title = group.tag();
        if (title != null) {
            title = TagPrefix.forPath(modulePath).stripTagPrefixIfPresent(title);
            title = "[" + title + "] - " + group.authorDate();
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

    private void merge(
            List<Item> markdown, FormattedRelease formattedRelease, FormatOptions formatOptions, boolean overwrite) {
        // generate the new Markdown sections based on the formatted release
        Map<String, Section> generatedSections = new LinkedHashMap<>();
        for (FormattedRelease.Group formattedGroup : formattedRelease.groups()) {
            String title = formattedGroup.title();
            Section newSection = new Section(2, title, formatSectionBody(formattedGroup));
            generatedSections.put(title, newSection);
        }
        boolean hasUnreleasedSection = generatedSections.containsKey(formatOptions.defaultTag());

        // get the top level Markdown section of the existing changelog
        Section topLevelSection = markdown.stream()
                .filter(Section.class::isInstance)
                .map(Section.class::cast)
                .filter(s -> s.level() == 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Changelog must have a top level heading"));

        Set<String> existingTitles = topLevelSection.contents().stream()
                .filter(Section.class::isInstance)
                .map(Section.class::cast)
                .map(Section::title)
                .collect(Collectors.toSet());

        int i = 0;
        boolean isFirstTime = true;
        while (i < topLevelSection.contents().size()) {
            if (topLevelSection.contents().get(i) instanceof Section existingSection) {
                if (!hasUnreleasedSection && existingSection.title().equalsIgnoreCase(formatOptions.defaultTag())) {
                    // remove existing unreleased section
                    topLevelSection.contents().remove(i);
                } else {
                    if (isFirstTime) {
                        isFirstTime = false;

                        // insert all new sections before the current position
                        for (Section newSection : generatedSections.values()) {
                            if (!existingTitles.contains(newSection.title())) {
                                topLevelSection.contents().add(i, newSection);
                                i++;
                            }
                        }
                    }
                    // replace contents if new section exists, let it be otherwise
                    Optional.ofNullable(generatedSections.get(existingSection.title()))
                            .map(Section::contents)
                            .ifPresent(newContents -> {
                                if (overwrite || existingSection.title().equalsIgnoreCase(formatOptions.defaultTag())) {
                                    existingSection.contents().clear();
                                    existingSection.contents().addAll(newContents);
                                }
                            });
                    i++;
                }
            } else {
                // skip over text content
                i++;
            }
        }

        if (isFirstTime) {
            // the changelog did not have any existing sections, so just add all new sections
            for (Section newSection : generatedSections.values()) {
                topLevelSection.contents().add(newSection);
            }
        }
    }

    private static List<Item> formatSectionBody(FormattedRelease.Group formattedGroup) {
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
}
