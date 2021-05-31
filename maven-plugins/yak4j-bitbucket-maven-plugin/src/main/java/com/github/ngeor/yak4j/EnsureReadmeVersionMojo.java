package com.github.ngeor.yak4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Ensures that the README.md file matches the version in the pom.xml.
 */
@Mojo(name = "ensure-readme-version", defaultPhase = LifecyclePhase.VALIDATE)
public class EnsureReadmeVersionMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.version}")
    private String version;

    @Override
    public void execute() throws MojoExecutionException {
        final Log log = getLog();
        TextUtil textUtil = new TextUtil();
        doExecute(log, textUtil);
    }

    /**
     * Runs the mojo.
     */
    void doExecute(Log log, TextUtil textUtil) throws MojoExecutionException {
        Pattern pattern = Pattern.compile("<version>(.+?)</version>");
        boolean hasErrors = false;
        try {
            List<String[]> matches = textUtil.filter(new File("README.md"), pattern);
            for (String[] match : matches) {
                String line = match[0];
                String foundVersion = match[1];
                if (!version.equals(foundVersion)) {
                    log.error(String.format(
                        "README.md version mismatch on line %s. Specified version %s should be %s.",
                        line, foundVersion, version));
                    hasErrors = true;
                } else {
                    log.info(String.format("Found correct version on line %s", line));
                }
            }
        } catch (FileNotFoundException ex) {
            throw new MojoExecutionException("Could not find README.md file: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }

        if (hasErrors) {
            throw new MojoExecutionException(
                "One or more lines specify an incorrect version, please check the logs above for details");
        }
    }

    void setVersion(String version) {
        this.version = version;
    }
}
