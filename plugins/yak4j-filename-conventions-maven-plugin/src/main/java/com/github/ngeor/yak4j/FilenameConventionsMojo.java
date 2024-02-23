package com.github.ngeor.yak4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

/**
 * A mojo that checks filename conventions.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VALIDATE)
public class FilenameConventionsMojo extends AbstractMojo {
    @Parameter(required = true)
    private File directory;

    @Parameter(required = true)
    private String[] includes;

    @Parameter
    private String[] excludes;

    @Parameter(required = true)
    private String[] pattern;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        try {
            final List<Pattern> compiledPatterns = compilePatterns();

            log.debug("Getting filenames");
            final List<String> fileNames = FileUtils.getFileNames(
                    directory, String.join(",", includes), excludes != null ? String.join(",", excludes) : null, false);

            for (String fileName : fileNames) {
                log.debug(String.format("Testing filename %s", fileName));
                final boolean matches = testFileName(log, compiledPatterns, fileName);
                if (!matches) {
                    throw new MojoFailureException(
                            String.format("Filename %s did not match any of the given patterns", fileName));
                }
            }

            log.info(String.format("%d file(s) in directory %s verified", fileNames.size(), directory));

        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new MojoFailureException(ex.getMessage());
        }
    }

    private boolean testFileName(Log log, List<Pattern> compiledPatterns, String fileName) {
        boolean matches = false;
        for (Pattern p : compiledPatterns) {
            Matcher m = p.matcher(fileName);
            matches = m.matches();
            log.debug(String.format("Tested filename %s against pattern %s => %s", fileName, p, matches));
            if (matches) {
                break;
            }
        }

        return matches;
    }

    private List<Pattern> compilePatterns() {
        assertNotEmpty(pattern, "No patterns were specified");

        List<Pattern> result = new ArrayList<>();
        for (String regex : pattern) {
            assertNotEmpty(regex, "Empty pattern was given");
            result.add(Pattern.compile(regex));
        }

        return result;
    }

    private static <E> void assertNotEmpty(E[] array, String message) {
        if (array == null || array.length <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void assertNotEmpty(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
