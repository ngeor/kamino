package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitVersionCommand {
    private final File rootDirectory;
    private final Git git;
    private final String path;

    public GitVersionCommand(File rootDirectory, String path) {
        this.rootDirectory = rootDirectory;
        this.git = new Git(rootDirectory);
        this.path = path;
    }

    public void run() throws IOException, ProcessFailedException, InterruptedException {
        List<String> paths =
                path == null ? new ModuleFinder().eligibleModules(rootDirectory).toList() : List.of(path);
        for (String p : paths) {
            run(p);
        }
    }

    private void run(String path) throws IOException, ProcessFailedException, InterruptedException {
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
