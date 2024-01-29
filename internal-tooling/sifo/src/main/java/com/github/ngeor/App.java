package com.github.ngeor;

import java.io.File;
import java.io.IOException;
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

        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isBlank()) {
            throw new IllegalStateException("GITHUB_TOKEN env variable is not configured");
        }

        File monorepoRoot = detectRootDirectory();
        String typeName = args[0];
        String projectName = args[1];
        File oldRepo =
                detectRootDirectory().toPath().resolveSibling(projectName).toFile().getAbsoluteFile();
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
