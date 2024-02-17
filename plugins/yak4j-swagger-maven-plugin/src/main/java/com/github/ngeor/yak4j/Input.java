package com.github.ngeor.yak4j;

import java.io.File;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * An input definition.
 */
public class Input {
    /**
     * The input swagger file.
     */
    @Parameter(required = true)
    private File file;

    /**
     * An optional prefix to prepend to all endpoints defined in this file. This
     * value can be used to avoid conflicts when merging swagger files that contain
     * endpoints with the same path.
     * <p>
     * Example: "/api-gateway"
     */
    @Parameter
    private String pathPrefix;

    /**
     * An optional prefix to prepend to all models defined in this file. This value
     * can be used to avoid conflicts when merging swagger files that contain models
     * with the same name.
     */
    @Parameter
    private String definitionPrefix;

    public File getFile() {
        return file;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public String getDefinitionPrefix() {
        return definitionPrefix;
    }

    @Override
    public String toString() {
        return "Input{" + "file=" + file + ", pathPrefix='" + pathPrefix + '\'' + ", definitionPrefix='"
                + definitionPrefix + '\'' + '}';
    }
}
