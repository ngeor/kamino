package com.github.ngeor;

import com.github.ngeor.git.Git;
import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.concurrent.ConcurrentException;

public class GitVersionCommand extends BaseCommand {
    private final File rootDirectory;
    private final Git git;
    private final String path;

    public GitVersionCommand(File rootDirectory, Map<String, Object> args) {
        super(rootDirectory, args);
        this.rootDirectory = rootDirectory;
        this.git = new Git(rootDirectory);
        this.path = (String) args.get("path");
    }

    @Override
    public void run() throws ProcessFailedException, ConcurrentException {
        List<String> paths =
                path == null ? new ModuleFinder().eligibleModules(rootDirectory).toList() : List.of(path);
        for (String p : paths) {
            run(p);
        }
    }

    private void run(String path) throws ProcessFailedException {
        GitVersionCalculator.Result result =
                new GitVersionCalculator(git, path).calculateGitVersion().orElse(null);
        if (result == null) {
            System.out.printf("No relevant tags for %s, perhaps never released?%n", path);
        } else if (result.bump() == null) {
            System.out.printf("No relevant commits in %s since %s%n", path, result.mostRecentVersion());
        } else {
            System.out.printf(
                    "Next version of %s should be %s (%s bump from %s)%n",
                    path, result.nextVersion(), result.bump(), result.mostRecentVersion());
        }
    }
}
