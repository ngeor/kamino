package com.github.ngeor;

import com.github.ngeor.changelog.CommitFilter;
import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.tuple.Pair;

public class ChangesOverviewCommand extends BaseCommand {
    private final File rootDirectory;
    private final Git git;

    public ChangesOverviewCommand(File rootDirectory, Map<String, Object> args) {
        super(rootDirectory, args);
        this.rootDirectory = rootDirectory;
        this.git = new Git(rootDirectory);
    }

    @Override
    public void run() throws ConcurrentException {
        System.out.println("Release status");

        List<List<String>> table = new ArrayList<>();
        table.add(new ArrayList<>(List.of("Module", "Latest version", "Date", "Number of unreleased commits")));
        new ModuleFinder()
                .eligibleModules(rootDirectory)
                .map(this::buildOverview)
                .forEach(table::add);

        TableFormatter.padColumns(
                table,
                List.of(
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.LEFT,
                        TableFormatter.Alignment.RIGHT));
        TableFormatter.printTable(table);
    }

    private List<String> buildOverview(String module) {
        List<String> result = new ArrayList<>(List.of(module));
        Pair<Tag, String> p = addTag(module);
        Tag tag = p.getLeft();
        // add message for tag
        result.add(p.getRight());
        result.add(tag == null ? "N/A" : tag.date());
        result.add(addCount(module, tag));
        return result;
    }

    private Pair<Tag, String> addTag(String module) {
        try {
            Tag tag = git.getMostRecentTagWithDate(TagPrefix.forPath(module).tagPrefix())
                    .orElseThrow();
            return Pair.of(tag, TagPrefix.forPath(module).stripTagPrefix(tag).toString());
        } catch (ProcessFailedException ex) {
            return Pair.of(null, ex.getMessage());
        } catch (NoSuchElementException ignored) {
            return Pair.of(null, "N/A");
        }
    }

    private String addCount(String module, Tag tag) {
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
}
