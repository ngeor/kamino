package com.github.ngeor.yak4j;

import static com.github.ngeor.yak4j.Util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

/**
 * A mojo that synchronizes from the archetype.
 */
@Mojo(name = "sync")
public class SyncArchetypeMojo extends AbstractMojo {
    @Component(role = MavenSession.class)
    private MavenSession session;

    @Component(role = MojoExecution.class)
    private MojoExecution execution;

    @Parameter(required = true)
    private String archetypeGroupId;

    @Parameter(required = true)
    private String archetypeArtifactId;

    @Parameter
    private String archetypeVersion;

    @Parameter(defaultValue = "${project.groupId}")
    private String groupId;

    @Parameter(defaultValue = "${project.artifactId}")
    private String artifactId;

    @Parameter(defaultValue = "${project.version}")
    private String version;

    @Parameter
    private Map<String, String> parameters;

    @Parameter(required = true)
    private String[] includes;

    @Parameter
    private String[] excludes;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        try {
            File tempFolder = createTempFolder(log);
            generateArchetype(log, tempFolder);
            copyFiles(log, tempFolder);
            deleteTempFolder(log, tempFolder);
        } catch (IOException | InterruptedException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private File createTempFolder(Log log) throws IOException {
        log.debug("Creating temp folder");
        File temp = Files.createTempDirectory(artifactId).toFile();
        log.info(String.format("Created temp folder %s", temp));
        return temp;
    }

    private void generateArchetype(Log log, File tempFolder)
            throws IOException, InterruptedException, MojoFailureException {
        log.info("Generating archetype in temp folder");
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String cmd = isWindows ? "mvn.cmd" : "mvn";
        ProcessBuilder processBuilder = new ProcessBuilder(combine(
                        Arrays.asList(
                                cmd,
                                "archetype:generate",
                                "-DinteractiveMode=false",
                                "-DarchetypeArtifactId=" + archetypeArtifactId,
                                "-DarchetypeGroupId=" + archetypeGroupId,
                                concatNull("-DarchetypeVersion=", archetypeVersion),
                                "-DgroupId=" + groupId,
                                "-DartifactId=" + artifactId,
                                "-Dversion=" + version),
                        formatParameters(parameters)))
                .directory(tempFolder)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new MojoFailureException("Failed to generate archetype");
        }
    }

    private void copyFiles(Log log, File tempFolder) throws IOException {
        log.info("Updating files in current project");

        File sourceDirectory = new File(tempFolder, artifactId);
        File destinationDirectory = session.getCurrentProject().getBasedir();

        List<File> files = FileUtils.getFiles(
                sourceDirectory, String.join(",", includes), excludes != null ? String.join(",", excludes) : null);

        for (File file : files) {
            copy(file, sourceDirectory, destinationDirectory);
        }
    }

    private void copy(File file, File sourceDirectory, File destinationDirectory) throws IOException {
        File parentFile = file.getParentFile();
        Path relativePath = sourceDirectory.toPath().relativize(parentFile.toPath());
        File destinationParentFile =
                destinationDirectory.toPath().resolve(relativePath).toFile();
        destinationParentFile.mkdirs();
        FileUtils.copyFileToDirectory(file, destinationParentFile);
    }

    private void deleteTempFolder(Log log, File tempFolder) throws IOException {
        log.info("Deleting temp folder");
        FileUtils.deleteDirectory(tempFolder);
    }
}
