package com.github.ngeor;

import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.maven.resolve.PomRepository;
import com.github.ngeor.maven.resolve.input.Input;
import com.github.ngeor.maven.resolve.input.ParentInputIterator;
import com.github.ngeor.mr.Defaults;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
    private final PomRepository pomRepository = new PomRepository();
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
    }

    private List<String> tags() {
        try {
            return lazyTags.get();
        } catch (ConcurrentException ex) {
            throw new ConcurrentRuntimeException(ex);
        }
    }

    private File rootModulePomXmlFile() {
        return new File(rootDirectory, "pom.xml");
    }

    private List<String> lazyModules;

    private Stream<String> modules() {
        if (lazyModules == null) {
            lazyModules = DomHelper.getModules(
                            pomRepository.load(rootModulePomXmlFile()).document())
                    .toList();
        }
        return lazyModules.stream();
    }

    private Map<MavenCoordinates, String> lazyCoordinateToModuleName;

    private Map<MavenCoordinates, String> coordinateToModuleNameMap() {
        if (lazyCoordinateToModuleName == null) {
            lazyCoordinateToModuleName = modules()
                    .collect(Collectors.toMap(
                            name -> pomRepository.load(modulePomXmlFile(name)).coordinates(), Function.identity()));
        }
        return lazyCoordinateToModuleName;
    }

    private File modulePomXmlFile(String module) {
        return rootDirectory.toPath().resolve(module).resolve("pom.xml").toFile();
    }

    private Optional<String> findModuleName(MavenCoordinates coordinates) {
        return Optional.ofNullable(coordinateToModuleNameMap().get(coordinates));
    }

    public void regenerateAllTemplates() throws IOException, ProcessFailedException {
        for (String module : modules().toList()) {
            regenerateAllTemplates(module);
        }
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

    public void regenerateAllTemplates(String module) throws IOException, ProcessFailedException {
        System.out.printf("Regenerating templates for %s%n", module);
        Input input = pomRepository.loadAndResolveProperties(modulePomXmlFile(module));
        DocumentWrapper doc = input.document();
        MavenCoordinates coordinates = input.coordinates();

        final String javaVersion = DomHelper.getProperty(doc, "maven.compiler.source")
                .map(String::trim)
                .map(v -> "1.8".equals(v) ? "8" : v)
                .orElse(DEFAULT_JAVA_VERSION);

        Input loadedInput = pomRepository.load(modulePomXmlFile(module));
        SortedSet<String> internalDependencies = Stream.concat(
                        // internal dependencies of module
                        internalDependenciesRecursively(coordinates),
                        // register also any internal snapshot parent poms as dependencies for this purpose
                        parentPomSnapshots(loadedInput))
                .collect(Collectors.toCollection(TreeSet::new));

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
        DocumentWrapper document = pomRepository
                .load(rootDirectory.toPath().resolve(module).resolve("pom.xml").toFile())
                .document();
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
                Map<MavenCoordinates, String> internalDependencies = pomRepository.findKnownFile(next).stream()
                        .map(pomRepository::loadAndResolveProperties)
                        .map(Input::document)
                        .flatMap(DomHelper::getDependencies)
                        .map(dep -> Map.entry(dep, findModuleName(dep)))
                        .filter(e -> e.getValue().isPresent())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, e -> e.getValue().get()));
                queue.addAll(internalDependencies.keySet());
                result.addAll(internalDependencies.values());
            }
        }
        return result.stream();
    }

    // must not be parent resolved or property resolved
    // TODO perhaps a way to check that it is not parent resolved or property resolved
    private Stream<String> parentPomSnapshots(Input loadedInput) {
        ParentInputIterator parentInputIterator = new ParentInputIterator(loadedInput, pomRepository);
        Iterable<Input> iterable = () -> parentInputIterator;
        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(i -> i.coordinates().version().endsWith("-SNAPSHOT"))
                .flatMap(i -> findModuleName(i.coordinates()).stream());
    }
}
