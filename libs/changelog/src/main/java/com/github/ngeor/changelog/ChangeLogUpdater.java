package com.github.ngeor.changelog;

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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ChangeLogUpdater {
    private final Options options;
    private final Git git;
    private final TagPrefix tagPrefix;

    public ChangeLogUpdater(Options options) {
        this.options = Objects.requireNonNull(options);
        this.git = new Git(options.rootDirectory());
        this.tagPrefix = TagPrefix.forPath(options.modulePath().orElse(null));
    }

    public void updateChangeLog() throws IOException, ProcessFailedException {
        // TODO parallelize this part with the part that parses the changelog
        // NOTE: Optional is only used as syntactic sugar, to have the pipeline-style chaining.
        FormattedRelease formattedRelease = Optional.of(getCommits())
                .map(this::groupCommits)
                .map(this::createRelease)
                .map(this::formatRelease)
                .orElseThrow();

        File changeLogFile = getChangeLogFile();
        List<Item> markdown = parseExistingChangeLog(changeLogFile);
        merge(markdown, formattedRelease);
        saveChangeLog(changeLogFile, markdown);
    }

    private File getChangeLogFile() {
        Path rootPath = options.rootDirectory().toPath();
        Path projectPath = options.modulePath().map(rootPath::resolve).orElse(rootPath);
        return projectPath.resolve("CHANGELOG.md").toFile();
    }

    private Stream<Commit> getCommits() throws ProcessFailedException {
        return git.revList((String) null, options.modulePath().orElse(null));
    }

    private List<List<Commit>> groupCommits(Stream<Commit> commits) {
        CommitGrouper commitGrouper = new CommitGrouper(tagPrefix);
        return commitGrouper.fromCommits(commits);
    }

    private Release createRelease(List<List<Commit>> commitGroups) {
        SubGroupOptions subGroupOptions =
                new SubGroupOptions("chore", List.of("feat", "fix", "chore", "deps"), this::overrideType);
        ReleaseGrouper releaseGrouper = new ReleaseGrouper(subGroupOptions, tagPrefix);
        return releaseGrouper.fromCommitGroups(commitGroups);
    }

    private String overrideType(CommitInfo commitInfo) {
        return "chore".equals(commitInfo.type()) && "deps".equals(commitInfo.scope())
                ? "deps"
                : ("refactor".equals(commitInfo.type()) ? "chore" : commitInfo.type());
    }

    private FormattedRelease formatRelease(Release release) {
        Formatter formatter = new Formatter(
                options.formatOptions(), tagPrefix, options.futureVersion().orElse(null));
        return formatter.format(release);
    }

    private static List<Item> parseExistingChangeLog(File changeLog) throws IOException {
        return changeLog.isFile()
                ? new MarkdownReader().read(changeLog)
                : new ArrayList<>(List.of(new Section(1, "Changelog")));
    }

    private void merge(List<Item> markdown, FormattedRelease formattedRelease) {
        MarkdownMerger markdownMerger = new MarkdownMerger(options.formatOptions(), options.overwrite());
        markdownMerger.mergeIntoLeft(markdown, formattedRelease);
    }

    private static void saveChangeLog(File changeLogFile, List<Item> markdown) throws IOException {
        MarkdownWriter markdownWriter = new MarkdownWriter();
        markdownWriter.write(markdown, changeLogFile);
    }
}
