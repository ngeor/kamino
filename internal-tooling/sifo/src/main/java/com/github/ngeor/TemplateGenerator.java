package com.github.ngeor;

import com.github.ngeor.yak4jdom.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    private final SimpleStringTemplate rootPomTemplate = SimpleStringTemplate.ofResource("/root-pom-template.xml");
    private final File root;
    private List<MavenModule> modules;

    public TemplateGenerator(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
    }

    private List<MavenModule> getModules() {
        if (modules == null) {
            modules = collectModules();
        }
        return modules;
    }

    public void regenerateAllTemplates()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        for (MavenModule module : getModules()) {
            regenerateAllTemplates(module);
        }
        regenerateRootPom();
    }

    private List<MavenModule> collectModules() {
        List<MavenModule> modules = new ArrayList<>();
        for (File typeDirectory : getDirectories(root)) {
            for (File projectDirectory : getDirectories(typeDirectory)) {
                File pomFile = new File(projectDirectory, "pom.xml");
                if (pomFile.isFile()) {
                    MavenModule module = new MavenModule(typeDirectory, projectDirectory, pomFile);
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    private void regenerateRootPom() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (MavenModule module : getModules()) {
            if (!builder.isEmpty()) {
                builder.append("\n").append("    ");
            }
            builder.append("<module>").append(module.path()).append("</module>");
        }

        // regenerate root pom
        Files.writeString(
                root.toPath().resolve("pom.xml"), rootPomTemplate.render(Map.of("modules", builder.toString())));
    }

    public void regenerateAllTemplates(MavenModule module)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        System.out.println("Regenerating templates for " + module.projectDirectory());
        String javaVersion = calculateJavaVersion(module).orElse(DEFAULT_JAVA_VERSION);
        // TODO detect monorepo dependencies of project and update the build template's paths so that the upstream
        // projects build
        String buildCommand;
        if (usesInternalDependencies(module)) {
            buildCommand = "mvn -B -ntp -pl " + module.path() + " -am clean verify";
        } else {
            buildCommand = "mvn -B -ntp clean verify --file " + module.path() + "/"
                    + module.pomFile().getName();
        }
        Map<String, String> variables = Map.of(
                "name",
                module.projectDirectory().getName(),
                "group",
                module.typeDirectory().getName(),
                "path",
                module.path(),
                "javaVersion",
                javaVersion,
                "buildCommand",
                buildCommand);

        String workflowId = module.path().replace('/', '-');
        Files.writeString(
                root.toPath().resolve(".github").resolve("workflows").resolve("build-" + workflowId + ".yml"),
                buildTemplate.render(variables));

        if (requiresReleaseWorkflow(module.typeDirectory().getName())) {
            Files.writeString(
                    root.toPath().resolve(".github").resolve("workflows").resolve("release-" + workflowId + ".yml"),
                    releaseTemplate.render(variables));
        }

        fixProjectUrls(module);
        sortPom(module.projectDirectory());

        fixProjectBadges(module.typeDirectory(), module.projectDirectory());
    }

    private boolean usesInternalDependencies(MavenModule module)
            throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        Set<MavenCoordinates> internalDependencies = getModules().stream()
                .map(m -> {
                    try {
                        return m.coordinates();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        return module.dependencies().anyMatch(internalDependencies::contains);
    }

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(MavenModule module)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document document = XmlUtils.parse(module.pomFile());

        XmlUtils.setChildText(document.getDocumentElement(), "groupId", GROUP_ID);
        XmlUtils.setChildText(
                document.getDocumentElement(),
                "artifactId",
                module.projectDirectory().getName());

        // TODO do not hardcode the github URL
        String url = "https://github.com/ngeor/kamino/tree/master/" + module.path();
        XmlUtils.setChildText(document.getDocumentElement(), "url", url);

        Element scm = XmlUtils.ensureChild(document.getDocumentElement(), "scm");
        XmlUtils.setChildText(scm, "connection", "scm:git:https://github.com/ngeor/kamino.git");
        XmlUtils.setChildText(scm, "developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        XmlUtils.setChildText(scm, "tag", "HEAD");
        XmlUtils.setChildText(scm, "url", url);

        XmlUtils.write(document, module.pomFile());
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

    private static Optional<String> calculateJavaVersion(MavenModule module) throws IOException, InterruptedException {
        String effectivePom = module.effectivePom();

        Pattern pattern = Pattern.compile("<maven.compiler.source>([0-9]+)</maven.compiler.source>");
        return effectivePom
                .lines()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .findFirst();
    }
}
