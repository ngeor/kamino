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
        // new TemplateGenerator(detectRootDirectory()).regenerateAllTemplates();

        //        new ProjectImporter(
        //            detectRootDirectory(),
        //            new File("C:\\Users\\ngeor\\Projects\\github\\yak4j-spring-test-utils"),
        //            "libs"
        //        ).run();

        new ReleasePerformer(detectRootDirectory(), "libs", "yak4j-spring-test-utils")
                .performPatchRelease(SemVer.parse("0.21.1"));
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
