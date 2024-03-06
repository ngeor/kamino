package com.github.ngeor;

import com.github.ngeor.changelog.ChangeLogUpdater;
import com.github.ngeor.changelog.ImmutableOptions;
import com.github.ngeor.mr.Defaults;
import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ChangeLogUpdaterCommand extends BaseCommand {
    private final File rootDirectory;
    private final String path;
    private final boolean overwrite;

    public ChangeLogUpdaterCommand(File rootDirectory, Map<String, Object> args) {
        super(rootDirectory, args);
        this.rootDirectory = rootDirectory;
        this.path = (String) args.get("path");
        this.overwrite = args.containsKey("overwrite");
    }

    @Override
    public void run() throws IOException, ProcessFailedException {
        List<String> paths =
                path == null ? new ModuleFinder().eligibleModules(rootDirectory).toList() : List.of(path);
        for (String p : paths) {
            new ChangeLogUpdater(ImmutableOptions.builder()
                            .rootDirectory(rootDirectory)
                            .modulePath(p)
                            .formatOptions(Defaults.defaultFormatOptions())
                            .overwrite(overwrite)
                            .build())
                    .updateChangeLog();
        }
    }
}
