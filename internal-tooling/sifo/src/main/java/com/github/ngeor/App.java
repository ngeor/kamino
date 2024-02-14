package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * Hello world!
 */
public final class App {
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        if (args == null || args.length == 0) {
            new TemplateGenerator(detectRootDirectory()).regenerateAllTemplates();
            return;
        }

        if ("release".equals(args[0])) {
            prepareRelease(args);
            return;
        }

        importOldProject(args);
    }

    private static void prepareRelease(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            throw new IllegalStateException("Expected at least 3 arguments");
        }

        File monorepoRoot = detectRootDirectory();
        String project = args[1];
        SemVerBump bump = Objects.requireNonNull(SemVerBump.parse(args[2]));
        String[] parts = project.split("/");
        boolean dryRun = args.length >= 4 && args[3].contains("dry");
        new ReleasePerformer(monorepoRoot, parts[0], parts[1]).performRelease(bump, dryRun);
    }

    private static void importOldProject(String[] args)
            throws IOException, InterruptedException, ParserConfigurationException, TransformerException, SAXException {
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
