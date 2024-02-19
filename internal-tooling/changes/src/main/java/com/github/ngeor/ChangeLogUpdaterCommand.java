package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChangeLogUpdaterCommand {
    private final File rootDirectory;
    private final String path;
    private final String version;

    public ChangeLogUpdaterCommand(File rootDirectory, String path, String version) {
        this.rootDirectory = rootDirectory;
        this.path = path;
        if (path == null && version != null) {
            throw new IllegalArgumentException("version must be null when path is null");
        }
        this.version = version;
    }

    public void run() throws IOException, ProcessFailedException, InterruptedException {
        List<String> paths =
                path == null ? new ModuleFinder().eligibleModules(rootDirectory).toList() : List.of(path);
        for (String p : paths) {
            new ChangeLogUpdater(rootDirectory, p).updateChangeLog(version);
        }
    }
}
