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

    private final StringTemplate buildTemplate = StringTemplate.ofResource("/build-template.yml");
    private final StringTemplate releaseTemplate = StringTemplate.ofResource("/release-template.yml");
    private final File root;

    public TemplateGenerator(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
    }

    public void regenerateAllTemplates()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        for (File typeLevel : getDirectories(root)) {
            for (File projectLevel : getDirectories(typeLevel)) {
                File pomFile = new File(projectLevel, "pom.xml");
                if (pomFile.isFile()) {
                    regenerateAllTemplates(typeLevel, projectLevel, pomFile);
                }
            }
        }
    }

    public void regenerateAllTemplates(File typeLevel, File projectLevel, File pomFile)
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

        if (requiresReleaseWorkflow(typeLevel.getName())) {
            Files.writeString(
                    root.toPath()
                            .resolve(".github")
                            .resolve("workflows")
                            .resolve("release-" + typeLevel.getName() + "-" + projectLevel.getName() + ".yml"),
                    releaseTemplate.render(variables));
        }

        fixProjectUrls(typeLevel, projectLevel, pomFile);
        sortPom(pomFile);

        fixProjectBadges(typeLevel, projectLevel, pomFile);
    }

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(File typeLevel, File projectLevel, File pomFile)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document document = XmlUtils.parse(pomFile);

        XmlUtils.setChildText(document.getDocumentElement(), "groupId", GROUP_ID);
        XmlUtils.setChildText(document.getDocumentElement(), "artifactId", projectLevel.getName());

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
        Maven maven = new Maven(pomFile.getParentFile());
        maven.sortPom();
    }

    private void fixProjectBadges(File typeLevel, File projectLevel, File pomFile) throws IOException {
        File readmeFile = pomFile.toPath().resolveSibling("README.md").toFile();
        if (!readmeFile.exists()) {
            // TODO create if it does not exist
            return;
        }

        String groupId = GROUP_ID;
        String artifactId = projectLevel.getName();
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
                    String url = "https://github.com/ngeor/kamino/actions/workflows/build-" + typeLevel.getName() + "-"
                            + projectLevel.getName() + ".yml";
                    line = String.format("[![Build %s](%s/badge.svg)](%s)", projectLevel.getName(), url, url);
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
