package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ChangeLogUpdaterCommand extends BaseCommand {
    private final File rootDirectory;
    private final String path;
    private final String version;
    private final boolean overwrite;

    public ChangeLogUpdaterCommand(File rootDirectory, Map<String, Object> args) {
        super(rootDirectory, args);
        this.rootDirectory = rootDirectory;
        this.path = (String) args.get("path");
        this.version = (String) args.get("version");
        this.overwrite = args.containsKey("overwrite");
        if (path == null && version != null) {
            throw new IllegalArgumentException("version must be null when path is null");
        }
    }

    @Override
    public void run() throws IOException, ProcessFailedException, InterruptedException {
        List<String> paths =
                path == null ? new ModuleFinder().eligibleModules(rootDirectory).toList() : List.of(path);
        for (String p : paths) {
            new ChangeLogUpdater(rootDirectory, p).updateChangeLog(version, overwrite);
        }
    }
}
