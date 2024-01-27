package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Hello world!
 */
public final class App {
    private final StringTemplate buildTemplate = StringTemplate.ofResource("/build-template.yml");
    private final StringTemplate releaseTemplate = StringTemplate.ofResource("/release-template.yml");
    private final File root;

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        new App(new File("../../")).run();
    }

    private App(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
    }

    private void run()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        for (File typeLevel : getDirectories(root)) {
            for (File projectLevel : getDirectories(typeLevel)) {
                File pomFile = new File(projectLevel, "pom.xml");
                if (pomFile.isFile()) {
                    processProject(typeLevel, projectLevel, pomFile);
                }
            }
        }
    }

    private void processProject(File typeLevel, File projectLevel, File pomFile)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        System.out.println(projectLevel);
        String javaVersion = Objects.requireNonNullElse(calculateJavaVersion(pomFile), "11");
        Map<String, String> variables = Map.of(
                "name",
                projectLevel.getName(),
                "group",
                typeLevel.getName(),
                "path",
                typeLevel.getName() + "/" + projectLevel.getName(),
                "javaVersion",
                javaVersion);

        Files.writeString(
                root.toPath()
                        .resolve(".github")
                        .resolve("workflows")
                        .resolve("build-" + typeLevel.getName() + "-" + projectLevel.getName() + ".yml"),
                buildTemplate.render(variables));

        if (Set.of("archetypes", "libs").contains(typeLevel.getName())) {
            Files.writeString(
                    root.toPath()
                            .resolve(".github")
                            .resolve("workflows")
                            .resolve("release-" + typeLevel.getName() + "-" + projectLevel.getName() + ".yml"),
                    releaseTemplate.render(variables));
        }

        fixProjectUrls(typeLevel, projectLevel, pomFile);
        sortPom(pomFile);
    }

    private void fixProjectUrls(File typeLevel, File projectLevel, File pomFile)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document document = XmlUtils.parse(pomFile);

        // TODO do not hardcode the github URL
        String url =
                "https://github.com/ngeor/kamino/tree/master/" + typeLevel.getName() + "/" + projectLevel.getName();
        XmlUtils.setChildText(document.getDocumentElement(), "url", url);

        Element scm = XmlUtils.ensureChild(document.getDocumentElement(), "scm");
        XmlUtils.setChildText(scm, "connection", "scm:git:https://github.com/ngeor/kamino.git");
        XmlUtils.setChildText(scm, "developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        XmlUtils.setChildText(scm, "tag", "HEAD");
        XmlUtils.setChildText(scm, "url", url);

        XmlUtils.write(document, pomFile);
    }

    private void sortPom(File pomFile) throws IOException, InterruptedException {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        int status = new ProcessBuilder(cmd, "-B", "-ntp", "-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .directory(pomFile.getParentFile())
                .start()
                .waitFor();
        if (status != 0) {
            throw new IllegalStateException("sort pom failed");
        }
    }

    private static File[] getDirectories(File file) {
        return file.listFiles(new DirectoryFileFilter());
    }

    private static String calculateJavaVersion(File pomFile) throws IOException {
        // TODO use effective pom command instead
        Pattern pattern = Pattern.compile("<java.version>([0-9]+)</java.version>");
        for (String line : Files.readAllLines(pomFile.toPath())) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
