package com.github.ngeor.yak4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.xml.sax.SAXException;

/**
 * Ensures that modules in a multi-module pom have the correct parent pom version.
 */
@Mojo(name = "ensure-modules-version", defaultPhase = LifecyclePhase.VALIDATE)
public class EnsureModulesVersionMojo extends AbstractXmlMojo {

    @Override
    void doExecute(Log log, XmlUtil xmlUtil) throws MojoExecutionException {
        try {
            List<String> modules = xmlUtil.getElementContents(new File("pom.xml"), "modules", "module");
            for (String module : modules) {
                File modulePomFile = new File(new File(module), "pom.xml");
                String textContent = String.join("", xmlUtil.getElementContents(modulePomFile, "parent", "version"));
                if (!getVersion().equals(textContent)) {
                    throw new MojoExecutionException(
                        String.format(
                            "Module %s has parent version %s but should match pom.xml version and have value %s",
                            module, textContent, getVersion()));
                } else {
                    log.info(
                        String.format(
                            "Module %s has parent version %s and matches parent pom.xml version",
                            module,
                            textContent));
                }
            }
        } catch (SAXException | IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }
}
