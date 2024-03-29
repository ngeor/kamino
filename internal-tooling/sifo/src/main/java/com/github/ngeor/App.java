package com.github.ngeor;

import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.io.IOException;

public final class App {
    public static void main(String[] args) throws IOException, InterruptedException, ProcessFailedException {
        if (args == null || args.length == 0) {
            new TemplateGenerator(detectRootDirectory()).regenerateAllTemplates();
            return;
        }

        importOldProject(args);
    }

    private static void importOldProject(String[] args)
            throws IOException, InterruptedException, ProcessFailedException {
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isBlank()) {
            throw new IllegalStateException("GITHUB_TOKEN env variable is not configured");
        }

        File monorepoRoot = detectRootDirectory();
        String typeName = args[0];
        String projectName = args[1];
        File oldRepo = detectRootDirectory()
                .toPath()
                .resolveSibling(projectName)
                .toFile()
                .getAbsoluteFile();
        new ProjectImporter(monorepoRoot, oldRepo, typeName, githubToken).run();
    }

    private static File detectRootDirectory() {
        File file = new File(".").getAbsoluteFile();
        while (!looksLikeRootDirectory(file)) {
            file = file.getParentFile();
        }

        return file;
    }

    private static boolean looksLikeRootDirectory(File file) {
        return file.isDirectory() && new File(file, ".git").isDirectory() && new File(file, ".github").isDirectory();
    }
}
