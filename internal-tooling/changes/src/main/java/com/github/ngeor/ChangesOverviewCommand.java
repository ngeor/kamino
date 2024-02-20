package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

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
        System.out.println("Release status");
        System.out.println("Module\tLatest version\tDate\tNumber of unreleased commits");
        new ModuleFinder()
                .eligibleModules(rootDirectory)
                .map(this::buildOverview)
                .forEach(System.out::println);
    }

    private String buildOverview(String module) {
        try {
            return module + "\t"
                    + recentTagWithDate(module)
                            .map(tag -> buildExtraInfo(module, tag))
                            .orElse("N/A");
        } catch (IOException | InterruptedException | ProcessFailedException e) {
            return module + "\t" + e.getMessage();
        }
    }

    private Optional<Tag> recentTagWithDate(String module)
            throws IOException, InterruptedException, ProcessFailedException {
        return git.getMostRecentTagWithDate(TagPrefix.tagPrefix(module));
    }

    private String buildExtraInfo(String module, Tag tag) {
        String count;
        try {
            count = String.valueOf(git.revList(tag, module)
                    .map(Commit::summary)
                    .filter(new CommitFilter())
                    .count());
        } catch (Exception ex) {
            count = ex.getMessage();
        }
        return TagPrefix.version(module, tag) + "\t" + tag.date() + "\t" + count;
    }
}
