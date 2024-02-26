package com.github.ngeor.changelog;

import com.github.ngeor.changelog.format.FormatOptions;
import com.github.ngeor.changelog.format.FormattedRelease;
import com.github.ngeor.changelog.format.Formatter;
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
import java.util.stream.Stream;

public class ChangeLogUpdater {
    private final File rootDirectory;
    private final String modulePath;
    private final Git git;
    private final FormatOptions formatOptions;
    private final TagPrefix tagPrefix;

    public ChangeLogUpdater(File rootDirectory, String modulePath, FormatOptions formatOptions) {
        this.rootDirectory = rootDirectory;
        this.modulePath = modulePath;
        this.git = new Git(rootDirectory);
        this.formatOptions = Objects.requireNonNull(formatOptions);
        this.tagPrefix = TagPrefix.forPath(modulePath);
    }

    private File getChangeLog() {
        Path rootPath = rootDirectory.toPath();
        Path projectPath = modulePath == null ? rootPath : rootPath.resolve(modulePath);
        return projectPath.resolve("CHANGELOG.md").toFile();
    }

    void updateChangeLog() throws IOException, ProcessFailedException {
        updateChangeLog(false);
    }

    public void updateChangeLog(boolean overwrite) throws IOException, ProcessFailedException {
        updateChangeLog(overwrite, null);
    }

    public void updateChangeLog(boolean overwrite, SemVer futureVersion) throws IOException, ProcessFailedException {
        List<Item> markdown = generateChangeLog(overwrite, futureVersion);
        new MarkdownWriter().write(markdown, getChangeLog());
    }

    private List<Item> generateChangeLog(boolean overwrite, SemVer futureVersion)
            throws IOException, ProcessFailedException {

        SubGroupOptions subGroupOptions =
                new SubGroupOptions("chore", List.of("feat", "fix", "chore", "deps"), this::remapType);

        ReleaseGrouper releaseGrouper = new ReleaseGrouper(subGroupOptions);
        Release release = releaseGrouper.toRelease(getCommits());
        FormattedRelease formattedRelease = new Formatter(formatOptions, tagPrefix, futureVersion).format(release);
        File changeLog = getChangeLog();
        List<Item> markdown = changeLog.isFile()
                ? new MarkdownReader().read(changeLog)
                : new ArrayList<>(List.of(new Section(1, "Changelog")));
        new MarkdownMerger(formatOptions, overwrite).mergeIntoLeft(markdown, formattedRelease);
        return markdown;
    }

    private String remapType(CommitInfo commitInfo) {
        return "chore".equals(commitInfo.type()) && "deps".equals(commitInfo.scope())
                ? "deps"
                : ("refactor".equals(commitInfo.type()) ? "chore" : commitInfo.type());
    }

    private Stream<Commit> getCommits() throws ProcessFailedException {
        return git.revList((String) null, modulePath);
    }
}
