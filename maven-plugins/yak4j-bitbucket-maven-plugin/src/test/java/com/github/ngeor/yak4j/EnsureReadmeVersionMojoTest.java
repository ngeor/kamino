package com.github.ngeor.yak4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EnsureReadmeVersionMojo}.
 */
class EnsureReadmeVersionMojoTest {
    @Test
    void success() throws IOException, MojoExecutionException {
        // arrange
        EnsureReadmeVersionMojo mojo = new EnsureReadmeVersionMojo();
        Log log = mock(Log.class);
        TextUtil textUtil = mock(TextUtil.class);
        mojo.setVersion("1.2.3");
        when(textUtil.filter(any(File.class), any())).thenReturn(
            Collections.singletonList(
                new String[]{"some line 1.2.3", "1.2.3"}
            )
        );

        // act
        mojo.doExecute(log, textUtil);

        // assert
        verify(log).info("Found correct version on line some line 1.2.3");
    }

    @Test
    void fail() throws IOException, MojoExecutionException {
        // arrange
        EnsureReadmeVersionMojo mojo = new EnsureReadmeVersionMojo();
        Log log = mock(Log.class);
        TextUtil textUtil = mock(TextUtil.class);
        mojo.setVersion("1.2.3");
        when(textUtil.filter(any(File.class), any())).thenReturn(
            Collections.singletonList(
                new String[]{"some line 1.2.4", "1.2.4"}
            )
        );

        // act
        assertThatThrownBy(() -> mojo.doExecute(log, textUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessage("One or more lines specify an incorrect version, please check the logs above for details");

        // assert
        verify(log).error(
            "README.md version mismatch on line some line 1.2.4. Specified version 1.2.4 should be 1.2.3.");
    }

    @Test
    void fileNotFound() throws IOException, MojoExecutionException {
        // arrange
        EnsureReadmeVersionMojo mojo = new EnsureReadmeVersionMojo();
        Log log = mock(Log.class);
        TextUtil textUtil = mock(TextUtil.class);
        mojo.setVersion("1.2.3");
        when(textUtil.filter(any(File.class), any())).thenThrow(new FileNotFoundException());

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, textUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessageStartingWith("Could not find README.md file");
    }

    @Test
    void ioException() throws IOException, MojoExecutionException {
        // arrange
        EnsureReadmeVersionMojo mojo = new EnsureReadmeVersionMojo();
        Log log = mock(Log.class);
        TextUtil textUtil = mock(TextUtil.class);
        mojo.setVersion("1.2.3");
        when(textUtil.filter(any(File.class), any())).thenThrow(new IOException("oops"));

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(log, textUtil))
            .isInstanceOf(MojoExecutionException.class)
            .hasMessageStartingWith("oops");
    }
}
