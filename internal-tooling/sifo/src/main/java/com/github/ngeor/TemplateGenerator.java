package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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

public final class TemplateGenerator {
    private static final String GROUP_ID = "com.github.ngeor";
    private static final String DEFAULT_JAVA_VERSION = "11";

    private final SimpleStringTemplate buildTemplate = SimpleStringTemplate.ofResource("/build-template.yml");
    private final SimpleStringTemplate releaseTemplate = SimpleStringTemplate.ofResource("/release-template.yml");
    private final File root;

    public TemplateGenerator(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
    }

    public void regenerateAllTemplates()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        for (File typeDirectory : getDirectories(root)) {
            for (File projectDirectory : getDirectories(typeDirectory)) {
                File pomFile = new File(projectDirectory, "pom.xml");
                if (pomFile.isFile()) {
                    regenerateAllTemplates(typeDirectory, projectDirectory, pomFile);
                }
            }
        }
    }

    public void regenerateAllTemplates(File typeDirectory, File projectDirectory, File pomFile)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        System.out.println("Regenerating templates for " + projectDirectory);
        String javaVersion = Objects.requireNonNullElse(calculateJavaVersion(projectDirectory), DEFAULT_JAVA_VERSION);
        String buildCommand = "mvn -B -ntp clean verify --file " + typeDirectory.getName() + "/" + projectDirectory.getName() + "/pom.xml";
        if (projectDirectory.getName().equals("internal-tooling")) {
            buildCommand = "mvn -B -ntp -pl " + typeDirectory.getName() + "/" + projectDirectory.getName() + " clean verify";
        }
        Map<String, String> variables = Map.of(
                "name",
                projectDirectory.getName(),
                "group",
                typeDirectory.getName(),
                "path",
                typeDirectory.getName() + "/" + projectDirectory.getName(),
                "javaVersion",
                javaVersion,
            "buildCommand",
            buildCommand);

        Files.writeString(
                root.toPath()
                        .resolve(".github")
                        .resolve("workflows")
                        .resolve("build-" + typeDirectory.getName() + "-" + projectDirectory.getName() + ".yml"),
                buildTemplate.render(variables));

        if (requiresReleaseWorkflow(typeDirectory.getName())) {
            Files.writeString(
                    root.toPath()
                            .resolve(".github")
                            .resolve("workflows")
                            .resolve("release-" + typeDirectory.getName() + "-" + projectDirectory.getName() + ".yml"),
                    releaseTemplate.render(variables));
        }

        fixProjectUrls(typeDirectory, projectDirectory, pomFile);
        sortPom(projectDirectory);

        fixProjectBadges(typeDirectory, projectDirectory);
    }

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(File typeDirectory, File projectDirectory, File pomFile)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document document = XmlUtils.parse(pomFile);

        XmlUtils.setChildText(document.getDocumentElement(), "groupId", GROUP_ID);
        XmlUtils.setChildText(document.getDocumentElement(), "artifactId", projectDirectory.getName());

        // TODO do not hardcode the github URL
        String url = "https://github.com/ngeor/kamino/tree/master/" + typeDirectory.getName() + "/"
                + projectDirectory.getName();
        XmlUtils.setChildText(document.getDocumentElement(), "url", url);

        Element scm = XmlUtils.ensureChild(document.getDocumentElement(), "scm");
        XmlUtils.setChildText(scm, "connection", "scm:git:https://github.com/ngeor/kamino.git");
        XmlUtils.setChildText(scm, "developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        XmlUtils.setChildText(scm, "tag", "HEAD");
        XmlUtils.setChildText(scm, "url", url);

        XmlUtils.write(document, pomFile);
    }

    private void sortPom(File projectDirectory) throws IOException, InterruptedException {
        Maven maven = new Maven(projectDirectory);
        maven.sortPom();
    }

    private void fixProjectBadges(File typeDirectory, File projectDirectory) throws IOException {
        File readmeFile = projectDirectory.toPath().resolve("README.md").toFile();
        if (!readmeFile.exists()) {
            // TODO create if it does not exist
            return;
        }

        String groupId = GROUP_ID;
        String artifactId = projectDirectory.getName();
        boolean foundBadges = false;
        List<String> lines = new ArrayList<>(Files.readAllLines(readmeFile.toPath()));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("[![")) {
                foundBadges = true;

                // TODO add missing badges
                if (line.startsWith("[![Maven Central")) {
                    line = "[![Maven Central](https://img.shields.io/maven-central/v/" + groupId + "/" + artifactId
                            + ".svg?label=Maven%20Central)](https://central.sonatype.com/artifact/" + groupId + "/"
                            + artifactId + "/overview)";
                    lines.set(i, line);
                } else if (line.startsWith("[![Java CI") || line.startsWith("[![Build")) {
                    String url = "https://github.com/ngeor/kamino/actions/workflows/build-" + typeDirectory.getName()
                            + "-" + projectDirectory.getName() + ".yml";
                    line = String.format("[![Build %s](%s/badge.svg)](%s)", projectDirectory.getName(), url, url);
                    lines.set(i, line);
                } else if (line.startsWith("[![javadoc")) {
                    String badgeUrl = String.format("https://javadoc.io/badge2/%s/%s/javadoc.svg", groupId, artifactId);
                    String url = String.format("https://javadoc.io/doc/%s/%s", groupId, artifactId);
                    line = String.format("[![javadoc](%s)](%s)", badgeUrl, url);
                    lines.set(i, line);
                }
            } else {
                if (foundBadges) {
                    break;
                }
            }
        }
        Files.writeString(readmeFile.toPath(), String.join(System.lineSeparator(), lines) + System.lineSeparator());
    }

    private static File[] getDirectories(File file) {
        return file.listFiles(new DirectoryFileFilter());
    }

    private static String calculateJavaVersion(File projectDirectory) throws IOException, InterruptedException {
        File tempFile = File.createTempFile("pom", ".xml");
        tempFile.deleteOnExit();

        Maven maven = new Maven(projectDirectory);
        maven.effectivePom(tempFile);

        Pattern pattern = Pattern.compile("<maven.compiler.source>([0-9]+)</maven.compiler.source>");
        for (String line : Files.readAllLines(tempFile.toPath())) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
