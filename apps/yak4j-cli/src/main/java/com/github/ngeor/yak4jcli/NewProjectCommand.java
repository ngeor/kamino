package com.github.ngeor.yak4jcli;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import picocli.CommandLine;

/**
 * Creates a new project.
 */
@CommandLine.Command(
    name = "new",
    description = "Creates a new Java project inside this repo"
)
public class NewProjectCommand implements Runnable {
    @Override
    public void run() {
        //        String groupId = query("What is the group ID");
        String artifactId = query("What is the artifact ID");
        String name = query("What is the project name");
        //        String packageName = query("What is the package name");
        String subFolder = query("What is the root subfolder for this type of projects");
        //        Validate.notBlank(groupId, "Group id is mandatory");
        Validate.notBlank(artifactId, "Artifact id is mandatory");
        Validate.notBlank(name, "Name is mandatory");
        //        Validate.notBlank(packageName, "Package name is mandatory");

        Path destinationPath = (
            StringUtils.isBlank(subFolder)
                ? Paths.get(".", name)
                : Paths.get(".", subFolder, name)
        ).toAbsolutePath();

        try {
            ParentPomInfo parentPomInfo = locateParentPom(subFolder);

            createProjectRootDirectory(destinationPath);
            createPomFile(destinationPath, parentPomInfo, name, artifactId);
            registerModuleInParentPom(destinationPath, parentPomInfo);
            createDefaultDirectories(destinationPath);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createProjectRootDirectory(Path destinationPath) {
        destinationPath.toFile().mkdirs();
    }

    private void createPomFile(
        Path destinationPath,
        ParentPomInfo parentPomInfo,
        String name,
        String artifactId
    ) throws IOException {
        Map<String, Object> scope = new HashMap<>();
        // parent pom info
        scope.put("parentGroupId", parentPomInfo.getGroupId());
        scope.put("parentArtifactId", parentPomInfo.getArtifactId());
        scope.put("parentVersion", parentPomInfo.getVersion());
        scope.put("relativePath", useUnixStylePaths(destinationPath.relativize(parentPomInfo.getPath()).toString()));
        // project info
        scope.put("name", name);
        scope.put("artifactId", artifactId);
        scope.put("version", "1.0-SNAPSHOT");
        // render template
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile(
            new InputStreamReader(getClass().getResourceAsStream("/_pom.xml")),
            "_pom.xml"
        );
        try (FileWriter fileWriter = new FileWriter(destinationPath.resolve("pom.xml").toFile())) {
            mustache.execute(
                fileWriter,
                scope
            );
        }
    }

    private void registerModuleInParentPom(Path destinationPath, ParentPomInfo parentPomInfo) {
        // calculate module relative path
        Path parentPomPath = parentPomInfo.getPath();
        Path parentPath = parentPomPath.getParent();
        Path relative = parentPath.relativize(destinationPath);
        // parse dom
        DocumentWrapper document = DocumentWrapper.parse(parentPomPath.toFile());
        ElementWrapper projectElement = document.getDocumentElement();
        ElementWrapper modulesElement = projectElement.firstElement("modules")
            .orElseThrow(() -> new IllegalArgumentException("parent pom does not have modules element"));
        // append module
        ElementWrapper moduleElement = document.createElement("module");
        moduleElement.setTextContent(useUnixStylePaths(relative.toString()));
        modulesElement.appendChild(moduleElement);
        // write dom
        document.write(parentPomPath.toFile());
    }

    private static String useUnixStylePaths(String path) {
        return path == null ? null : path.replace('\\', '/');
    }

    private void createDefaultDirectories(Path destinationPath) {
        destinationPath.resolve("src/main/java").toFile().mkdirs();
    }

    private static Path safeGetParent(Path other) {
        if (other == null) {
            return null;
        }

        Path parent = other.getParent();
        if (other.equals(parent)) {
            return null;
        }

        return parent;
    }

    private ParentPomInfo locateParentPom(String subFolder) {
        Path currentPath = Paths.get(".").toAbsolutePath();
        Path subPath = StringUtils.isBlank(subFolder) ? currentPath : Paths.get(".", subFolder).toAbsolutePath();
        if (!subPath.startsWith(currentPath)) {
            throw new IllegalArgumentException(
                "Cannot create project outside root folder. " + subFolder + " must be within " + currentPath
            );
        }

        boolean foundPom = false;
        Path pomPath = null;
        File pom = null;
        while (!foundPom && subPath != null && subPath.startsWith(currentPath)) {
            pomPath = subPath.resolve("pom.xml");
            pom = pomPath.toFile();
            foundPom = pom.exists();
            if (!foundPom) {
                subPath = safeGetParent(subPath);
            }
        }

        if (!foundPom) {
            throw new IllegalArgumentException("Could not locate parent pom");
        }

        PomDocument document = PomDocument.parse(pom);
        if (!"pom".equals(document.getPackaging())) {
            throw new IllegalArgumentException("pom file " + pom + " was detected but it was not a parent pom");
        }
        String groupId = Validate.notBlank(
            document.getGroupId(),
            "Parent pom cannot have empty group id"
        );
        String artifactId = Validate.notBlank(
            document.getArtifactId(),
            "Parent pom cannot have empty artifact id"
        );
        String version = Validate.notBlank(
            document.getVersion(),
            "Parent pom cannot have empty version"
        );
        return new ParentPomInfo(
            pomPath,
            groupId,
            artifactId,
            version
        );
    }

    private String query(String prompt) {
        System.out.print(prompt + "? ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
