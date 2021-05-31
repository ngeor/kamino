package com.github.ngeor.yak4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EnsureModulesVersionMojo}.
 */
class EnsureModulesVersionMojoTest {
    @Test
    void success() throws IOException, SAXException, MojoExecutionException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsureModulesVersionMojo mojo = new EnsureModulesVersionMojo();
        mojo.setVersion("1.2.3");
        when(xmlUtil.getElementContents(new File("pom.xml"), "modules", "module"))
            .thenReturn(Arrays.asList("one", "two"));
        when(xmlUtil.getElementContents(
            new File(new File("one"), "pom.xml"),
        "parent", "version")).thenReturn(Collections.singletonList("1.2.3"));
        when(xmlUtil.getElementContents(
            new File(new File("two"), "pom.xml"),
            "parent", "version")).thenReturn(Collections.singletonList("1.2.3"));

        // act
        mojo.doExecute(log, xmlUtil);

        // assert
        verify(log).info("Module one has parent version 1.2.3 and matches parent pom.xml version");
        verify(log).info("Module two has parent version 1.2.3 and matches parent pom.xml version");
    }

    @Test
    void fail() throws IOException, SAXException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsureModulesVersionMojo mojo = new EnsureModulesVersionMojo();
        mojo.setVersion("1.2.3");
        when(xmlUtil.getElementContents(new File("pom.xml"), "modules", "module"))
            .thenReturn(Arrays.asList("one", "two"));
        when(xmlUtil.getElementContents(
            new File(new File("one"), "pom.xml"),
            "parent", "version")).thenReturn(Collections.singletonList("1.2.4"));

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, xmlUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessage("Module one has parent version 1.2.4 but should match pom.xml version and have value 1.2.3");
    }

    @Test
    void saxException() throws IOException, SAXException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsureModulesVersionMojo mojo = new EnsureModulesVersionMojo();
        mojo.setVersion("1.2.3");
        when(xmlUtil.getElementContents(new File("pom.xml"), "modules", "module"))
            .thenThrow(new SAXException("oops"));

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, xmlUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessage("oops");
    }

    @Test
    void ioException() throws IOException, SAXException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsureModulesVersionMojo mojo = new EnsureModulesVersionMojo();
        mojo.setVersion("1.2.3");
        when(xmlUtil.getElementContents(new File("pom.xml"), "modules", "module"))
            .thenThrow(new IOException("oops"));

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, xmlUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessage("oops");
    }
}
