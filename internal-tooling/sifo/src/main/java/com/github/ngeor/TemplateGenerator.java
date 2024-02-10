package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
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
import org.xml.sax.SAXException;

public final class TemplateGenerator {
    private static final String GROUP_ID = "com.github.ngeor";
    private static final String DEFAULT_JAVA_VERSION = "11";

    private final SimpleStringTemplate buildTemplate = SimpleStringTemplate.ofResource("/build-template.yml");
    private final SimpleStringTemplate releaseTemplate = SimpleStringTemplate.ofResource("/release-template.yml");
    private final SimpleStringTemplate rootPomTemplate = SimpleStringTemplate.ofResource("/root-pom-template.xml");
    private final File root;
    private final MavenModules modules;

    public TemplateGenerator(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
        this.modules = new MavenModules(root);
    }

    private List<MavenModule> getModules() {
        return modules.getModules();
    }

    public void regenerateAllTemplates()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        for (MavenModule module : getModules()) {
            regenerateAllTemplates(module);
        }
        regenerateRootPom();
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
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        System.out.println("Regenerating templates for " + module.projectDirectory());
        String javaVersion = calculateJavaVersion(module).orElse(DEFAULT_JAVA_VERSION);
        String buildCommand;
        String extraPaths;
        List<MavenModule> internalDependencies = new ArrayList<>();
        modules.visitDependenciesRecursively(module, internalDependencies::add);
        if (internalDependencies.isEmpty()) {
            buildCommand = "mvn -B -ntp clean verify --file " + module.path() + "/"
                    + module.pomFile().getName();
            extraPaths = "";
        } else {
            buildCommand = "mvn -B -ntp -pl " + module.path() + " -am clean verify";
            extraPaths = internalDependencies.stream()
                    .map(dep -> System.lineSeparator() + "      - " + dep.path() + "/**")
                    .sorted()
                    .collect(Collectors.joining());
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
                buildCommand,
                "extraPaths",
                extraPaths);

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

        new ReadmeGenerator().fixProjectBadges(module.projectDirectory(), GROUP_ID, workflowId);
    }

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(MavenModule module) throws IOException, InterruptedException {
        boolean hadChanges = false;
        DocumentWrapper document = DocumentWrapper.parse(module.pomFile());

        hadChanges |= document.getDocumentElement().ensureChildText("groupId", GROUP_ID);
        hadChanges |= document.getDocumentElement()
                .ensureChildText("artifactId", module.projectDirectory().getName());

        // TODO do not hardcode the github URL
        String url = "https://github.com/ngeor/kamino/tree/master/" + module.path();
        hadChanges |= document.getDocumentElement().ensureChildText("url", url);

        ElementWrapper scm = document.getDocumentElement().ensureChild("scm");
        hadChanges |= scm.ensureChildText("connection", "scm:git:https://github.com/ngeor/kamino.git");
        hadChanges |= scm.ensureChildText("developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        hadChanges |= scm.ensureChildText("tag", "HEAD");
        hadChanges |= scm.ensureChildText("url", url);

        if (hadChanges) {
            document.write(module.pomFile());
            Maven maven = new Maven(module.pomFile());
            maven.sortPom();
        }
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
