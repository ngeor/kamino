package com.github.ngeor;

import com.github.ngeor.changelog.CommitFilter;
import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("java:S106")
public class ChangesOverviewCommand extends BaseCommand {
    private final File rootDirectory;
    private final Git git;

    public ChangesOverviewCommand(File rootDirectory, Map<String, Object> args) {
        super(rootDirectory, args);
        this.rootDirectory = rootDirectory;
        this.git = new Git(rootDirectory);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        System.out.println("Getting release information...");

        List<List<String>> table = new ArrayList<>();
        table.add(new ArrayList<>(
                List.of("Module", "Latest version", "Next version", "Date", "Number of unreleased commits")));
        new ModuleFinder()
                .eligibleModules(rootDirectory)
                .parallel()
                .map(this::buildOverview)
                .sorted(Comparator.comparing(o -> o.get(0)))
                .forEachOrdered(table::add);

        TableFormatter.padColumns(
                table,
                List.of(
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.RIGHT));
        TableFormatter.printTable(table);

        System.out.printf("Done in %dmsec%n", System.currentTimeMillis() - now);
    }

    private List<String> buildOverview(String module) {
        return new ModuleOverviewBuilder(git, module).buildOverview();
    }

    private record ModuleOverviewBuilder(Git git, String module) {
        public List<String> buildOverview() {
            List<String> result = new ArrayList<>(List.of(module));
            Pair<Tag, String> p = addTag();
            Tag tag = p.getLeft();
            // add message for tag
            result.add(p.getRight());
            result.add(nextVersion(tag));
            result.add(tag == null ? "N/A" : tag.date());
            result.add(unreleasedCommitCount(tag));
            return result;
        }

        private Pair<Tag, String> addTag() {
            try {
                Tag tag = git.getMostRecentTagWithDate(TagPrefix.forPath(module).tagPrefix())
                        .orElseThrow();
                return Pair.of(
                        tag, TagPrefix.forPath(module).stripTagPrefix(tag).toString());
            } catch (ProcessFailedException ex) {
                return Pair.of(null, ex.getMessage());
            } catch (NoSuchElementException ignored) {
                return Pair.of(null, "N/A");
            }
        }

        private String unreleasedCommitCount(Tag tag) {
            if (tag != null) {
                try {
                    return String.valueOf(git.revList(tag, module)
                            .map(Commit::summary)
                            .filter(new CommitFilter())
                            .count());
                } catch (ProcessFailedException ex) {
                    return ex.getMessage();
                }
            } else {
                return "N/A";
            }
        }

        private String nextVersion(Tag tag) {
            SemVer recentVersion = Optional.ofNullable(tag)
                    .map(Tag::name)
                    .map(n -> TagPrefix.forPath(module).stripTagPrefixIfPresent(n))
                    .flatMap(SemVer::tryParse)
                    .orElse(null);
            if (recentVersion == null) {
                return "N/A";
            }

            GitVersionCalculator calculator = new GitVersionCalculator(git, module);
            try {
                GitVersionCalculator.Result result = calculator.calculateGitVersion(recentVersion);
                return result.nextVersion().toString();
            } catch (ProcessFailedException e) {
                return e.getMessage();
            }
        }
    }
}
