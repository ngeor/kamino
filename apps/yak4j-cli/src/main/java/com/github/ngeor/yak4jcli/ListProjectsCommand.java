package com.github.ngeor.yak4jcli;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import picocli.CommandLine;

import static com.github.ngeor.yak4jcli.DomUtil.getChildElement;
import static com.github.ngeor.yak4jcli.DomUtil.getChildElements;

/**
 * Lists all projects inside the repo.
 */
@CommandLine.Command(name = "list", description = "Lists all projects inside the repo")
public class ListProjectsCommand implements Runnable {
    @Override
    public void run() {
        try {
            File rootPomFile = new File("pom.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(rootPomFile);
            Element projectElement = document.getDocumentElement();
            Element modulesElement = getChildElement(projectElement, "modules").orElse(null);
            if (modulesElement == null) {
                return;
            }

            getChildElements(modulesElement, "module").forEach(moduleElement -> {
                System.out.println(moduleElement.getTextContent());
                // TODO ensure child pom exists. If also packaging pom, recurse.
            });
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
