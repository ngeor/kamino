package com.github.ngeor.changelog;

import com.github.ngeor.changelog.format.FormatOptions;
import com.github.ngeor.changelog.format.FormattedRelease;
import com.github.ngeor.changelog.format.Formatter;
import com.github.ngeor.changelog.group.CommitGrouper;
import com.github.ngeor.changelog.group.CommitInfo;
import com.github.ngeor.changelog.group.Release;
import com.github.ngeor.changelog.group.ReleaseGrouper;
import com.github.ngeor.changelog.group.SubGroupOptions;
import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.markdown.Item;
import com.github.ngeor.markdown.MarkdownReader;
import com.github.ngeor.markdown.MarkdownWriter;
import com.github.ngeor.markdown.Section;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ChangeLogUpdater {
    private final File rootDirectory;
    private final String modulePath;
    private final Git git;
    private final FormatOptions formatOptions;
    private final TagPrefix tagPrefix;

    public ChangeLogUpdater(File rootDirectory, String modulePath, FormatOptions formatOptions) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.modulePath = modulePath;
        this.git = new Git(rootDirectory);
        this.formatOptions = Objects.requireNonNull(formatOptions);
        this.tagPrefix = TagPrefix.forPath(modulePath);
    }

    void updateChangeLog() throws IOException, ProcessFailedException {
        updateChangeLog(false);
    }

    public void updateChangeLog(boolean overwrite) throws IOException, ProcessFailedException {
        updateChangeLog(overwrite, null);
    }

    public void updateChangeLog(boolean overwrite, SemVer futureVersion) throws IOException, ProcessFailedException {
        FormattedRelease formattedRelease = Optional.of(getCommits())
                .map(this::groupCommits)
                .map(this::createRelease)
                .map(r -> formatRelease(r, futureVersion))
                .orElseThrow();

        File changeLogFile = getChangeLogFile();
        List<Item> markdown = parseExistingChangeLog(changeLogFile);
        merge(markdown, formattedRelease, overwrite);
        saveChangeLog(changeLogFile, markdown);
    }

    private File getChangeLogFile() {
        Path rootPath = rootDirectory.toPath();
        Path projectPath = modulePath == null ? rootPath : rootPath.resolve(modulePath);
        return projectPath.resolve("CHANGELOG.md").toFile();
    }

    private Stream<Commit> getCommits() throws ProcessFailedException {
        return git.revList((String) null, modulePath);
    }

    private List<List<Commit>> groupCommits(Stream<Commit> commits) {
        CommitGrouper commitGrouper = new CommitGrouper();
        return commitGrouper.fromCommits(commits);
    }

    private Release createRelease(List<List<Commit>> commitGroups) {
        SubGroupOptions subGroupOptions =
                new SubGroupOptions("chore", List.of("feat", "fix", "chore", "deps"), this::overrideType);
        ReleaseGrouper releaseGrouper = new ReleaseGrouper(subGroupOptions);
        return releaseGrouper.fromCommitGroups(commitGroups);
    }

    private String overrideType(CommitInfo commitInfo) {
        return "chore".equals(commitInfo.type()) && "deps".equals(commitInfo.scope())
                ? "deps"
                : ("refactor".equals(commitInfo.type()) ? "chore" : commitInfo.type());
    }

    private FormattedRelease formatRelease(Release release, SemVer futureVersion) {
        Formatter formatter = new Formatter(formatOptions, tagPrefix, futureVersion);
        return formatter.format(release);
    }

    private static List<Item> parseExistingChangeLog(File changeLog) throws IOException {
        return changeLog.isFile()
                ? new MarkdownReader().read(changeLog)
                : new ArrayList<>(List.of(new Section(1, "Changelog")));
    }

    private void merge(List<Item> markdown, FormattedRelease formattedRelease, boolean overwrite) {
        MarkdownMerger markdownMerger = new MarkdownMerger(formatOptions, overwrite);
        markdownMerger.mergeIntoLeft(markdown, formattedRelease);
    }

    private static void saveChangeLog(File changeLogFile, List<Item> markdown) throws IOException {
        MarkdownWriter markdownWriter = new MarkdownWriter();
        markdownWriter.write(markdown, changeLogFile);
    }
}
