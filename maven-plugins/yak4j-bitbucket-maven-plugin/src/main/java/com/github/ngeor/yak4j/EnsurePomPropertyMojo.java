package com.github.ngeor.yak4j;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

/**
 * Ensures that a pom property matches the version in the pom.xml.
 */
@Mojo(name = "ensure-pom-property", defaultPhase = LifecyclePhase.VALIDATE)
public class EnsurePomPropertyMojo extends AbstractXmlMojo {
    @Parameter(required = true)
    private String propertyName;

    @Override
    void doExecute(Log log, XmlUtil xmlUtil) throws MojoExecutionException {
        try {
            String textContent = String.join("",
                xmlUtil.getElementContents(new File("pom.xml"), "properties", propertyName));
            if (!getVersion().equals(textContent)) {
                throw new MojoExecutionException(
                    String.format(
                        "Property %s has value %s but should match pom.xml version and have value %s",
                        propertyName,
                        textContent,
                        getVersion()));
            } else {
                log.info(String.format(
                    "Property %s has value %s and matches pom.xml version",
                    propertyName,
                    textContent));
            }
        } catch (SAXException | IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
