package com.github.ngeor.yak4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

/**
 * A mojo that converts YAML to JSON.
 */
@Mojo(name = "yaml2json", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class YamlToJsonMojo extends AbstractMojo {
    @Parameter(required = true)
    private File sourceDirectory;

    @Parameter(required = true)
    private List<String> includes;

    @Parameter
    private List<String> excludes;

    @Parameter(required = true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        try {
            log.debug("Getting filenames");
            final List<File> files = FileUtils.getFiles(
                    sourceDirectory, String.join(",", includes), excludes != null ? String.join(",", excludes) : null);

            for (File file : files) {
                log.debug(String.format("Converting %s", file));
                convert(file, log);
            }

        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private File toOutputRelative(File file) {
        Path relativePath = sourceDirectory.toPath().relativize(file.toPath());
        return outputDirectory.toPath().resolve(relativePath).toFile();
    }

    private void convert(File file, Log log) throws IOException {
        // read file
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(file, Object.class);

        // create correct target directory
        File parentFile = file.getParentFile();
        File destinationParentFile = toOutputRelative(parentFile);
        log.debug(String.format("Creating directory %s", outputDirectory));
        destinationParentFile.mkdirs();

        // write file
        File resultFile = new File(destinationParentFile, FileUtils.removeExtension(file.getName()) + ".json");

        ObjectMapper jsonWriter = new ObjectMapper();
        jsonWriter.writeValue(resultFile, obj);
    }
}
