package com.github.ngeor.checkstyle.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Utility class for tests.
 */
public final class Utils {
    private static final String BASE_DIR = "src/test/resources";
    private static final String RULES_PACKAGE_NAME = "rules";
    private static final String RULES_PACKAGE_DIR = BASE_DIR + File.separator + RULES_PACKAGE_NAME;

    private Utils() {}

    /**
     * Creates the checkstyle configuration.
     */
    static Configuration createConfiguration() throws CheckstyleException {
        Configuration configuration = ConfigurationLoader.loadConfiguration(
                "src/main/resources/com/github/ngeor/checkstyle.xml", new PropertiesExpander(new Properties()));
        assertThat(configuration).isNotNull();
        return configuration;
    }

    /**
     * Creates the checker.
     */
    static Checker createChecker(AuditListener auditListener) throws CheckstyleException {
        Checker checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());
        checker.setBasedir(new File(BASE_DIR).getAbsolutePath());
        checker.configure(createConfiguration());
        checker.addListener(auditListener);
        return checker;
    }

    /**
     * Processes a file.
     */
    static int process(Checker checker, String file) throws CheckstyleException {
        return checker.process(List.of(new File(RULES_PACKAGE_DIR + File.separator + file).getAbsoluteFile()));
    }

    static String expectedFileName(String file) {
        return RULES_PACKAGE_NAME + File.separator + file;
    }
}
