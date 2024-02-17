package com.github.ngeor.yak4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * A mojo that merges swagger files.
 */
@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SwaggerMergerMojo extends AbstractMojo {
    /**
     * The file where the generated swagger file should be written to.
     */
    @Parameter(required = true)
    private File output;

    /**
     * A collection of input swagger files, together with their configuration.
     */
    @Parameter(required = true)
    private List<Input> inputs = new ArrayList<>();

    /**
     * An optional collection of files that will be inlined in the resulting swagger
     * document.
     */
    @Parameter
    private List<File> inlineDefinitions = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // load input documents
            List<SwaggerDocument> docs = getSwaggerDocuments();

            // load inline definitions (filename -> SwaggerDocument)
            Map<String, SwaggerDocument> inlineDocs = getInlineDefinitionsMap();

            // merge documents
            SwaggerDocument combined = docs.stream()
                    .reduce(this::merge)
                    .orElseThrow(() -> new MojoExecutionException("No input files found"));

            // inline definitions
            DefinitionInliner inliner = new DefinitionInliner();
            inliner.inline(combined, inlineDocs);

            // write to file
            writeToFile(combined);
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private void writeToFile(SwaggerDocument combined) throws IOException {
        Log log = getLog();

        SwaggerWriter writer = new SwaggerWriter();

        createDirectory();
        log.info(String.format("Writing file %s", output));
        try (FileOutputStream fileOutputStream = new FileOutputStream(output)) {
            writer.write(combined, fileOutputStream);
        }
    }

    private void createDirectory() {
        Log log = getLog();
        File parentFile = output.getParentFile();
        log.info(String.format("Creating directory %s", parentFile));
        parentFile.mkdirs();
    }

    private Map<String, SwaggerDocument> getInlineDefinitionsMap() throws IOException {
        Map<String, SwaggerDocument> inlineDocs = new HashMap<>();
        for (File file : inlineDefinitions) {
            inlineDocs.put(file.getName(), readPartialSwaggerDocument(file));
        }

        return inlineDocs;
    }

    private List<SwaggerDocument> getSwaggerDocuments() throws IOException {
        List<SwaggerDocument> docs = new ArrayList<>();
        for (Input input : inputs) {
            docs.add(readMainSwaggerDocument(input));
        }

        return docs;
    }

    private SwaggerDocument readMainSwaggerDocument(Input input) throws IOException {
        Log log = getLog();

        SwaggerParser parser = new SwaggerParser();
        DefinitionPrefixer definitionPrefixer = new DefinitionPrefixer();
        PathPrefixer pathPrefixer = new PathPrefixer();

        File file = input.getFile();
        log.info(String.format("Reading file %s", file));
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            SwaggerDocument swaggerDocument = parser.parse(fileInputStream);
            definitionPrefixer.prefix(swaggerDocument, input.getDefinitionPrefix());
            pathPrefixer.prefix(swaggerDocument, input.getPathPrefix());
            return swaggerDocument;
        }
    }

    private SwaggerDocument readPartialSwaggerDocument(File file) throws IOException {
        Log log = getLog();
        SwaggerParser parser = new SwaggerParser();
        log.info(String.format("Reading partial file %s", file));
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return parser.parse(fileInputStream);
        }
    }

    private SwaggerDocument merge(SwaggerDocument a, SwaggerDocument b) {
        Merger merger = new Merger();
        merger.merge(a, b);
        return a;
    }
}
