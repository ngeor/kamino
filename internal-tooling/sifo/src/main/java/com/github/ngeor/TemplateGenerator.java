package com.github.ngeor;

import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.ParentPom;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.maven.resolve.PomRepository;
import com.github.ngeor.maven.resolve.ResolutionPhase;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.stream.Streams;

public final class TemplateGenerator {
    private static final String GROUP_ID = "com.github.ngeor";
    private static final String DEFAULT_JAVA_VERSION = "11";

    private final SimpleStringTemplate buildTemplate;
    private final SimpleStringTemplate releaseTemplate;
    private final SimpleStringTemplate rootPomTemplate;
    private final File rootDirectory;
    private final PomRepository pomRepository;
    private final DocumentWrapper rootModule;
    private final Map<String, MavenCoordinates> resolvedModuleCoordinates;
    private final Map<MavenCoordinates, String> coordinatesToModule;

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
        // prime pom repository
        this.pomRepository = new PomRepository();
        this.rootModule = pomRepository.loadAndResolveProperties(new File(rootDirectory, "pom.xml").getCanonicalFile());
        // prime modules
        resolvedModuleCoordinates = DomHelper.getModules(rootModule
                )
                .collect(Collectors.toMap(moduleName -> moduleName, moduleName -> {
                    System.out.println("Loading module " + moduleName);
                    File file;
                    try {
                        file = rootDirectory
                                .toPath()
                                .resolve(moduleName)
                                .resolve("pom.xml")
                                .toFile()
                                .getCanonicalFile();
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                    return DomHelper.getCoordinates(pomRepository.loadAndResolveProperties(file));
                }));
        coordinatesToModule = resolvedModuleCoordinates.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private Stream<String> modules() {
        return DomHelper.getModules(rootModule
        );
    }

    public void regenerateAllTemplates() throws IOException {
        Streams.failableStream(modules()).forEach(this::regenerateAllTemplates);
        regenerateRootPom();
    }

    private void regenerateRootPom() throws IOException {
        StringBuilder builder = new StringBuilder();
        modules().forEach(module -> {
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

    public void regenerateAllTemplates(String module) throws IOException, ProcessFailedException, ConcurrentException {
        System.out.printf("Regenerating templates for %s%n", module);
        MavenCoordinates coordinates = resolvedModuleCoordinates.get(module);
        DocumentWrapper doc = pomRepository.resolveProperties(coordinates);
        final String javaVersion = DomHelper.getProperty(doc, "maven.compiler.source")
            .map(String::trim)
                .map(v -> "1.8".equals(v) ? "8" : v)
                .orElse(DEFAULT_JAVA_VERSION);

        String buildCommand;
        String extraPaths;
        SortedSet<String> internalDependencies = Stream.concat(
                        // internal dependencies of module
                        internalDependenciesRecursively(coordinates),
                        // register also any internal snapshot parent poms as dependencies for this purpose
                        parentPomSnapshots(coordinates))
                .collect(Collectors.toCollection(TreeSet::new));

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

        if (requiresReleaseWorkflow(typeDirectory(module))) {
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

        new ReadmeGenerator(rootDirectory, module, coordinates, workflowId).fixProjectBadges();
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

    public static boolean requiresReleaseWorkflow(String typeName) {
        return Set.of("archetypes", "libs", "plugins").contains(typeName);
    }

    private void fixProjectUrls(String module) throws ProcessFailedException {
        boolean hadChanges = false;
        DocumentWrapper document =
                pomRepository.getDocument(resolvedModuleCoordinates.get(module), ResolutionPhase.UNRESOLVED);
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

    private Stream<String> internalDependenciesRecursively(MavenCoordinates initialCoordinates) {
        Set<MavenCoordinates> seen = new HashSet<>();
        Queue<MavenCoordinates> queue = new LinkedList<>();
        Set<String> result = new TreeSet<>();
        for (MavenCoordinates next = initialCoordinates; next != null; next = queue.poll()) {
            if (seen.add(next)) {
                Set<MavenCoordinates> internalDependencies = DomHelper.getDependencies(
                    pomRepository.resolveProperties(next)
                )
                        .filter(coordinatesToModule::containsKey)
                        .collect(Collectors.toSet());
                queue.addAll(internalDependencies);
                internalDependencies.stream().map(coordinatesToModule::get).forEach(result::add);
            }
        }
        return result.stream();
    }

    private Stream<String> parentPomSnapshots(MavenCoordinates initialCoordinates) {
        MavenCoordinates next = initialCoordinates;
        List<String> result = new ArrayList<>();
        while (next != null) {
            ParentPom parentPom = pomRepository.getOriginalParentPom(next);
            String parentModule = null;
            if (parentPom != null) {
                next = parentPom.coordinates();
                if (next != null && next.version() != null && next.version().endsWith("-SNAPSHOT")) {
                    parentModule = coordinatesToModule.get(next);
                }
            }
            if (parentModule != null) {
                result.add(parentModule);
            } else {
                next = null;
            }
        }
        return result.stream();
    }
}
