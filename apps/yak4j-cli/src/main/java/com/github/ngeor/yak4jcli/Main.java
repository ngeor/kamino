package com.github.ngeor.yak4jcli;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Main class.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        new Main().createNewProject();
    }

    private void createNewProject() throws ParserConfigurationException, IOException, SAXException {
        //        String groupId = query("What is the group ID");
        //        String artifactId = query("What is the artifact ID");
        String name = query("What is the project name");
        //        String packageName = query("What is the package name");
        String subFolder = query("What is the root subfolder for this type of projects");
        //        Validate.notBlank(groupId, "Group id is mandatory");
        //        Validate.notBlank(artifactId, "Artifact id is mandatory");
        Validate.notBlank(name, "Name is mandatory");
        //        Validate.notBlank(packageName, "Package name is mandatory");

        ParentPomInfo parentPomInfo = locateParentPom(subFolder);

        Map<String, Object> scope = new HashMap<>();
        scope.put("parentGroupId", parentPomInfo.getGroupId());
        scope.put("parentArtifactId", parentPomInfo.getArtifactId());
        scope.put("parentVersion", parentPomInfo.getVersion());

        Path destinationPath = Paths.get(
            StringUtils.isBlank(subFolder) ? "." : subFolder,
            name
        ).toAbsolutePath();

        scope.put("relativePath", destinationPath.relativize(parentPomInfo.getPath()));

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile(
            new InputStreamReader(getClass().getResourceAsStream("/_pom.xml")),
            "_pom.xml"
        );
        mustache.execute(
            new PrintWriter(System.out),
            scope
        ).flush();
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

    private ParentPomInfo locateParentPom(String subFolder)
        throws ParserConfigurationException, IOException, SAXException {
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

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(pom);
        Element projectElement = document.getDocumentElement();
        String packaging = getXmlTextContent(projectElement, "packaging");
        if (!"pom".equals(packaging)) {
            throw new IllegalArgumentException("pom file " + pom + " was detected but it was not a parent pom");
        }
        String groupId = Validate.notBlank(
            getXmlTextContent(projectElement, "groupId"),
            "Parent pom cannot have empty group id"
        );
        String artifactId = Validate.notBlank(
            getXmlTextContent(projectElement, "artifactId"),
            "Parent pom cannot have empty artifact id"
        );
        String version = Validate.notBlank(
            getXmlTextContent(projectElement, "version"),
            "Parent pom cannot have empty version"
        );
        return new ParentPomInfo(
            pomPath,
            groupId,
            artifactId,
            version
        );
    }

    private String getXmlTextContent(Element element, String childNode) {
        if (element == null) {
            return null;
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE && childNode.equals(item.getNodeName())) {
                String textContent = item.getTextContent();
                return textContent == null ? null : textContent.trim();
            }
        }
        return null;
    }

    private String query(String prompt) {
        System.out.print(prompt + "? ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
