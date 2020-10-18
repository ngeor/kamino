package com.github.ngeor.yak4j;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for a mojo that needs to read XML files.
 */
public abstract class AbstractXmlMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.version}")
    private String version;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();
        DocumentBuilder documentBuilder;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new MojoFailureException(ex.getMessage(), ex);
        }

        XmlUtil xmlUtil = new XmlUtil(documentBuilder);

        doExecute(log, xmlUtil);
    }

    String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    abstract void doExecute(Log log, XmlUtil xmlUtil) throws MojoExecutionException;
}
