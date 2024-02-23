package com.github.ngeor;

import com.github.ngeor.maven.Maven;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.xml.sax.SAXException;

public final class TemplateGenerator {
    private static final String GROUP_ID = "com.github.ngeor";
    private static final String DEFAULT_JAVA_VERSION = "11";

    private final SimpleStringTemplate buildTemplate;
    private final SimpleStringTemplate releaseTemplate;
    private final SimpleStringTemplate rootPomTemplate;
    private final File root;
    private final MavenModules modules;

    public TemplateGenerator(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
        this.modules = new MavenModules(root);
        buildTemplate = SimpleStringTemplate.ofResource("/build-template.yml");
        releaseTemplate = SimpleStringTemplate.ofResource("/release-template.yml");
        rootPomTemplate = SimpleStringTemplate.ofResource("/root-pom-template.xml");
    }

    private SortedSet<MavenModule> getModules() {
        return modules.getModules();
    }

    public void regenerateAllTemplates()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException,
                    ProcessFailedException, ConcurrentException {
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
            throws IOException, InterruptedException, ParserConfigurationException, SAXException,
                    ProcessFailedException, ConcurrentException {
        System.out.println("Regenerating templates for " + module.projectDirectory());
        String javaVersion = module.calculateJavaVersion().orElse(DEFAULT_JAVA_VERSION);
        String buildCommand;
        String extraPaths;
        List<MavenModule> internalDependencies = new ArrayList<>();
        modules.visitDependenciesRecursively(module, internalDependencies::add);

        // register also any internal snapshot parent poms as dependencies for this purpose
        for (ParentPom p : module.parentPoms()) {
            if (p.relativePath() != null && p.coordinates().version().endsWith("SNAPSHOT")) {
                modules.getModules().stream()
                        .filter(m -> m.coordinates().equals(p.coordinates()))
                        .forEach(internalDependencies::add);
            }
        }

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
        Map<String, String> buildVariables = createTemplateVariables(module, javaVersion, buildCommand, extraPaths);

        String workflowId = module.path().replace('/', '-');
        Files.writeString(
                root.toPath().resolve(".github").resolve("workflows").resolve("build-" + workflowId + ".yml"),
                buildTemplate.render(buildVariables));

        if (requiresReleaseWorkflow(module.typeDirectory().getName())) {
            // needs to align with "arturito" release tooling
            final String releaseWorkflowJavaVersion = "17";
            Map<String, String> releaseVariables =
                    createTemplateVariables(module, releaseWorkflowJavaVersion, buildCommand, extraPaths);
            Files.writeString(
                    root.toPath().resolve(".github").resolve("workflows").resolve("release-" + workflowId + ".yml"),
                    releaseTemplate.render(releaseVariables));
        }

        fixProjectUrls(module);

        new ReadmeGenerator(root, module, workflowId).fixProjectBadges();
    }

    private static Map<String, String> createTemplateVariables(
            MavenModule module, String javaVersion, String buildCommand, String extraPaths) {
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
        return variables;
    }

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(MavenModule module) throws IOException, InterruptedException, ProcessFailedException {
        boolean hadChanges = false;
        DocumentWrapper document = DocumentWrapper.parse(module.pomFile());
        ElementWrapper documentElement = document.getDocumentElement();
        hadChanges |= ensureChildText(documentElement, "groupId", GROUP_ID);
        hadChanges |= ensureChildText(
                documentElement, "artifactId", module.projectDirectory().getName());

        // TODO do not hardcode the github URL
        String url = "https://github.com/ngeor/kamino/tree/master/" + module.path();
        hadChanges |= ensureChildText(documentElement, "url", url);

        ElementWrapper scm = documentElement.ensureChild("scm");
        hadChanges |= ensureChildText(scm, "connection", "scm:git:https://github.com/ngeor/kamino.git");
        hadChanges |= ensureChildText(scm, "developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        hadChanges |= ensureChildText(scm, "tag", "HEAD");
        hadChanges |= ensureChildText(scm, "url", url);

        if (hadChanges) {
            document.write(module.pomFile());
            Maven maven = new Maven(module.pomFile());
            maven.sortPom();
        }
    }

    private static boolean ensureChildText(ElementWrapper parent, String elementName, String text) {
        ElementWrapper child = parent.firstElement(elementName).orElse(null);
        boolean hadToCreate = child == null;
        if (hadToCreate) {
            child = parent.append(elementName);
        }
        if (text.trim().equals(child.getTextContentTrimmed().orElse(""))) {
            return hadToCreate;
        }
        child.setTextContent(text.trim());
        return true;
    }
}
