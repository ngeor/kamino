package com.github.ngeor;

import com.github.ngeor.maven.ChildMavenModule;
import com.github.ngeor.maven.Maven;
import com.github.ngeor.maven.MavenModule;
import com.github.ngeor.maven.RootMavenModule;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.stream.Streams;

public final class TemplateGenerator {
    private static final String GROUP_ID = "com.github.ngeor";
    private static final String DEFAULT_JAVA_VERSION = "11";

    private final SimpleStringTemplate buildTemplate;
    private final SimpleStringTemplate releaseTemplate;
    private final SimpleStringTemplate rootPomTemplate;
    private final File root;
    private final RootMavenModule rootModule;

    public TemplateGenerator(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
        this.rootModule = MavenModule.root(root.toPath().resolve("pom.xml").toFile());
        buildTemplate = SimpleStringTemplate.ofResource("/build-template.yml");
        releaseTemplate = SimpleStringTemplate.ofResource("/release-template.yml");
        rootPomTemplate = SimpleStringTemplate.ofResource("/root-pom-template.xml");
    }

    public void regenerateAllTemplates() throws IOException, ConcurrentException {
        Streams.failableStream(rootModule.children()).forEach(this::regenerateAllTemplates);
        regenerateRootPom();
    }

    private void regenerateRootPom() throws IOException, ConcurrentException {
        StringBuilder builder = new StringBuilder();
        rootModule.children().forEach(module -> {
            if (!builder.isEmpty()) {
                builder.append("\n").append("    ");
            }
            builder.append("<module>").append(module.getModuleName()).append("</module>");
        });

        // regenerate root pom
        Files.writeString(
                root.toPath().resolve("pom.xml"), rootPomTemplate.render(Map.of("modules", builder.toString())));
    }

    public void regenerateAllTemplates(ChildMavenModule module)
            throws IOException, ProcessFailedException, ConcurrentException {
        System.out.println("Regenerating templates for " + module.getModuleName());
        final String javaVersion = module.effectivePom()
                .property("maven.compiler.source")
                .map(v -> "1.8".equals(v) ? "8" : v)
                .orElse(DEFAULT_JAVA_VERSION);

        String buildCommand;
        String extraPaths;
        SortedSet<String> internalDependencies = Stream.concat(
                        // internal dependencies of module
                        module.internalDependenciesRecursively(),
                        // register also any internal snapshot parent poms as dependencies for this purpose
                        Streams.failableStream(module.parentPoms())
                                .filter(p -> p.relativePath() != null
                                        && p.coordinates().version().endsWith("SNAPSHOT"))
                                .map(p -> rootModule.children(
                                        p.coordinates().groupId(),
                                        p.coordinates().artifactId()))
                                .stream()
                                .flatMap(Function.identity()))
                .map(ChildMavenModule::getModuleName)
                .collect(Collectors.toCollection(TreeSet::new));

        if (internalDependencies.isEmpty()) {
            buildCommand = "mvn -B -ntp clean verify --file "
                    + root.toPath()
                            .relativize(module.getPomFile().toPath())
                            .toString()
                            .replace('\\', '/');
            extraPaths = "";
        } else {
            buildCommand = "mvn -B -ntp -pl " + module.getModuleName() + " -am clean verify";
            extraPaths = internalDependencies.stream()
                    .map(dep -> System.lineSeparator() + "      - " + dep + "/**")
                    .sorted()
                    .collect(Collectors.joining());
        }
        Map<String, String> buildVariables = createTemplateVariables(module, javaVersion, buildCommand, extraPaths);

        String workflowId = module.getModuleName().replace('/', '-');
        Files.writeString(
                root.toPath().resolve(".github").resolve("workflows").resolve("build-" + workflowId + ".yml"),
                buildTemplate.render(buildVariables));

        if (requiresReleaseWorkflow(typeDirectory(module).getName())) {
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

    private Map<String, String> createTemplateVariables(
            ChildMavenModule module, String javaVersion, String buildCommand, String extraPaths) {
        return Map.of(
                "name",
                projectDirectory(module).getName(),
                "group",
                typeDirectory(module).getName(),
                "path",
                module.getModuleName(),
                "javaVersion",
                javaVersion,
                "buildCommand",
                buildCommand,
                "extraPaths",
                extraPaths);
    }

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(ChildMavenModule module) throws ProcessFailedException {
        boolean hadChanges = false;
        DocumentWrapper document = DocumentWrapper.parse(module.getPomFile());
        ElementWrapper documentElement = document.getDocumentElement();
        hadChanges |= ensureChildText(documentElement, "groupId", GROUP_ID);
        hadChanges |= ensureChildText(
                documentElement, "artifactId", projectDirectory(module).getName());

        // TODO do not hardcode the github URL
        String url = "https://github.com/ngeor/kamino/tree/master/" + module.getModuleName();
        hadChanges |= ensureChildText(documentElement, "url", url);

        ElementWrapper scm = documentElement.ensureChild("scm");
        hadChanges |= ensureChildText(scm, "connection", "scm:git:https://github.com/ngeor/kamino.git");
        hadChanges |= ensureChildText(scm, "developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        hadChanges |= ensureChildText(scm, "tag", "HEAD");
        hadChanges |= ensureChildText(scm, "url", url);

        if (hadChanges) {
            document.write(module.getPomFile());
            Maven maven = new Maven(module.getPomFile());
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

    private File typeDirectory(ChildMavenModule module) {
        return projectDirectory(module).getParentFile();
    }

    private File projectDirectory(ChildMavenModule module) {
        return module.getPomFile().getParentFile();
    }
}
