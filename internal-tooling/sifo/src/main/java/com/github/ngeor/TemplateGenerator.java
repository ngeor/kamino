package com.github.ngeor;

import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.ng.EffectiveDocument;
import com.github.ngeor.maven.ng.PomDocument;
import com.github.ngeor.maven.ng.PomDocumentFactory;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.mr.Defaults;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentRuntimeException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

@SuppressWarnings("java:S106") // allow System.out.println
public final class TemplateGenerator {
    private static final String GROUP_ID = "com.github.ngeor";
    private static final String DEFAULT_JAVA_VERSION = "11";
    private final SimpleStringTemplate buildTemplate;
    private final SimpleStringTemplate releaseTemplate;
    private final SimpleStringTemplate rootPomTemplate;
    private final File rootDirectory;
    private final PomDocumentFactory pomDocumentFactory = new PomDocumentFactory();
    private final PomDocument aggregator;
    private final LazyInitializer<List<String>> lazyTags;

    public TemplateGenerator(File rootDirectory) throws IOException {
        // load templates
        buildTemplate = SimpleStringTemplate.ofResource("/build-template.yml");
        releaseTemplate = SimpleStringTemplate.ofResource("/release-template.yml");
        rootPomTemplate = SimpleStringTemplate.ofResource("/root-pom-template.xml");
        // validate rootDirectory
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        Validate.validState(rootDirectory.isDirectory(), "%s is not a directory", rootDirectory);
        Validate.validState(
                new File(rootDirectory, ".github").isDirectory(),
                "Could not find .github folder under %s",
                rootDirectory);
        lazyTags = LazyInitializer.<List<String>>builder()
                .setInitializer(() -> new Git(rootDirectory)
                        .getTags(null, false)
                        .map(Tag::name)
                        .toList())
                .get();
        this.aggregator = pomDocumentFactory.create(rootDirectory.toPath().resolve("pom.xml"));
    }

    private List<String> tags() {
        try {
            return lazyTags.get();
        } catch (ConcurrentException ex) {
            throw new ConcurrentRuntimeException(ex);
        }
    }

    public void regenerateAllTemplates() throws IOException, ProcessFailedException {
        for (String module : aggregator.modules().toList()) {
            regenerateAllTemplates(module);
        }
        regenerateRootPom();
    }

    private void regenerateRootPom() throws IOException {
        StringBuilder builder = new StringBuilder();
        aggregator.modules().forEach(module -> {
            if (!builder.isEmpty()) {
                builder.append("\n").append("    ");
            }
            builder.append("<module>").append(module).append("</module>");
        });

        // regenerate root pom
        Files.writeString(
                rootDirectory.toPath().resolve("pom.xml"),
                rootPomTemplate.render(Map.of("modules", builder.toString())));
    }

    public void regenerateAllTemplates(String module) throws IOException, ProcessFailedException {
        System.out.printf("Regenerating templates for %s%n", module);
        EffectiveDocument input = aggregator.loadModule(module).toEffective();
        DocumentWrapper doc = input.resolveProperties();
        MavenCoordinates coordinates = DomHelper.coordinates(doc);

        final String javaVersion = DomHelper.getProperty(doc, "maven.compiler.source")
                .map(String::trim)
                .map(v -> "1.8".equals(v) ? "8" : v)
                .orElse(DEFAULT_JAVA_VERSION);

        // internal dependencies of module
        SortedSet<String> internalDependencies = new TreeSet<>(aggregator.internalDependenciesOfModule(module));
        // register also any internal parent poms as dependencies for this purpose
        internalDependencies.addAll(aggregator.ancestorsOfModule(module));

        String buildCommand;
        String extraPaths;
        if (internalDependencies.isEmpty()) {
            buildCommand = "mvn -B -ntp clean verify --file " + module + "/pom.xml";
            extraPaths = "";
        } else {
            buildCommand = "mvn -B -ntp -pl " + module + " -am clean verify";
            extraPaths = internalDependencies.stream()
                    .map(dep -> System.lineSeparator() + "      - " + dep + "/**")
                    .sorted()
                    .collect(Collectors.joining());
        }
        Map<String, String> buildVariables = createTemplateVariables(module, javaVersion, buildCommand, extraPaths);

        String workflowId = module.replace('/', '-');
        Files.writeString(
                rootDirectory.toPath().resolve(".github").resolve("workflows").resolve("build-" + workflowId + ".yml"),
                buildTemplate.render(buildVariables));

        if (Defaults.isEligibleForRelease(module)) {
            // needs to align with "arturito" release tooling
            final String releaseWorkflowJavaVersion = "17";
            Map<String, String> releaseVariables =
                    createTemplateVariables(module, releaseWorkflowJavaVersion, buildCommand, extraPaths);
            Files.writeString(
                    rootDirectory
                            .toPath()
                            .resolve(".github")
                            .resolve("workflows")
                            .resolve("release-" + workflowId + ".yml"),
                    releaseTemplate.render(releaseVariables));
        }

        fixProjectUrls(module);

        new ReadmeGenerator(rootDirectory, module, coordinates, workflowId, this::tags).fixProjectBadges();
    }

    private Map<String, String> createTemplateVariables(
            String module, String javaVersion, String buildCommand, String extraPaths) {
        return Map.of(
                "name",
                projectDirectory(module),
                "group",
                typeDirectory(module),
                "path",
                module,
                "javaVersion",
                javaVersion,
                "buildCommand",
                buildCommand,
                "extraPaths",
                extraPaths);
    }

    private void fixProjectUrls(String module) throws ProcessFailedException {
        boolean hadChanges = false;
        DocumentWrapper document = aggregator.loadModule(module).loadDocument();
        ElementWrapper documentElement = document.getDocumentElement();
        hadChanges |= ensureChildText(documentElement, "groupId", GROUP_ID);
        hadChanges |= ensureChildText(documentElement, "artifactId", projectDirectory(module));

        // TODO do not hardcode the github URL
        String url = "https://github.com/ngeor/kamino/tree/master/" + module;
        hadChanges |= ensureChildText(documentElement, "url", url);

        ElementWrapper scm = documentElement.ensureChild("scm");
        hadChanges |= ensureChildText(scm, "connection", "scm:git:https://github.com/ngeor/kamino.git");
        hadChanges |= ensureChildText(scm, "developerConnection", "scm:git:git@github.com:ngeor/kamino.git");
        hadChanges |= ensureChildText(scm, "tag", "HEAD");
        hadChanges |= ensureChildText(scm, "url", url);

        if (hadChanges) {
            File pomFile =
                    rootDirectory.toPath().resolve(module).resolve("pom.xml").toFile();
            document.write(pomFile);
            Maven maven = new Maven(pomFile);
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

    private String typeDirectory(String module) {
        return module.split("/")[0];
    }

    private String projectDirectory(String module) {
        return module.split("/")[1];
    }
}
