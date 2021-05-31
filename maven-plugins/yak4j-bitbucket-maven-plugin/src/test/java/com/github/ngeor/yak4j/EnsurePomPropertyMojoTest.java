package com.github.ngeor.yak4j;

import java.io.File;
import java.io.IOException;
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
 * Unit tests for {@link EnsurePomPropertyMojo}.
 */
class EnsurePomPropertyMojoTest {
    @Test
    void success() throws MojoExecutionException, IOException, SAXException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsurePomPropertyMojo mojo = new EnsurePomPropertyMojo();
        mojo.setVersion("1.2.3");
        mojo.setPropertyName("my.property");
        when(xmlUtil.getElementContents(new File("pom.xml"), "properties", "my.property"))
            .thenReturn(Collections.singletonList("1.2.3"));

        // act
        mojo.doExecute(log, xmlUtil);

        // assert
        verify(log).info("Property my.property has value 1.2.3 and matches pom.xml version");
    }

    @Test
    void fail() throws IOException, SAXException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsurePomPropertyMojo mojo = new EnsurePomPropertyMojo();
        mojo.setVersion("1.2.3");
        mojo.setPropertyName("my.property");
        when(xmlUtil.getElementContents(new File("pom.xml"), "properties", "my.property"))
            .thenReturn(Collections.singletonList("1.2.4"));

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, xmlUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessage(
                "Property my.property has value 1.2.4 but should match pom.xml version and have value 1.2.3");
    }

    @Test
    void saxException() throws IOException, SAXException {
        // arrange
        Log log = mock(Log.class);
        XmlUtil xmlUtil = mock(XmlUtil.class);
        EnsurePomPropertyMojo mojo = new EnsurePomPropertyMojo();
        mojo.setVersion("1.2.3");
        mojo.setPropertyName("my.property");
        when(xmlUtil.getElementContents(new File("pom.xml"), "properties", "my.property"))
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
        EnsurePomPropertyMojo mojo = new EnsurePomPropertyMojo();
        mojo.setVersion("1.2.3");
        mojo.setPropertyName("my.property");
        when(xmlUtil.getElementContents(new File("pom.xml"), "properties", "my.property"))
            .thenThrow(new IOException("oops"));

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, xmlUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessage("oops");
    }
}
