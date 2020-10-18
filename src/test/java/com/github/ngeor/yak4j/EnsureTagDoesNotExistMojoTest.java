package com.github.ngeor.yak4j;

import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EnsureTagDoesNotExistMojo}.
 */
class EnsureTagDoesNotExistMojoTest {
    private EnsureTagDoesNotExistMojo mojo;

    @Mock
    private BitbucketApi bitbucketApi;

    @Mock
    private RestClient restClient;

    @Mock
    private Log log;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        mojo = new EnsureTagDoesNotExistMojo();
        mojo.setVersion("1.2.3");
        mojo.setUsername("username");
        mojo.setPassword("password");
        mojo.setOwner("owner");
        mojo.setSlug("slug");
    }

    @Test
    void doExecute() throws MojoFailureException, MojoExecutionException, IOException {
        // arrange
        when(bitbucketApi.tagExists("owner", "slug", "v1.2.3"))
            .thenReturn(false);
        when(bitbucketApi.tagOfBiggestVersion("owner", "slug"))
            .thenReturn("v1.2.2");

        // act
        mojo.doExecute(restClient, bitbucketApi, log);

        // assert
        verify(restClient).setCredentials(new Credentials("username", "password"));
        verify(log).info("Ensured that tag v1.2.3 does not exist and is allowed semver");
    }

    @Test
    void noTags() throws MojoFailureException, MojoExecutionException, IOException {
        // arrange
        when(bitbucketApi.tagExists("owner", "slug", "v1.2.3"))
            .thenReturn(false);
        when(bitbucketApi.tagOfBiggestVersion("owner", "slug"))
            .thenReturn(null);

        // act
        mojo.doExecute(restClient, bitbucketApi, log);

        // assert
        verify(restClient).setCredentials(new Credentials("username", "password"));
        verify(log).info("Ensured that tag v1.2.3 does not exist and is allowed semver");
    }

    @Test
    void tagExists() throws IOException {
        // arrange
        when(bitbucketApi.tagExists("owner", "slug", "v1.2.3"))
            .thenReturn(true);

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(restClient, bitbucketApi, log))
            .isInstanceOf(MojoFailureException.class)
            .hasMessage("Tag v1.2.3 already exists! Please bump the version in pom.xml");

        // assert
        verify(restClient).setCredentials(new Credentials("username", "password"));
    }

    @Test
    void semVerGap() throws IOException {
        // arrange
        when(bitbucketApi.tagExists("owner", "slug", "v1.2.3"))
            .thenReturn(false);
        when(bitbucketApi.tagOfBiggestVersion("owner", "slug"))
            .thenReturn("v1.1.1");

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(restClient, bitbucketApi, log))
            .isInstanceOf(MojoFailureException.class)
            .hasMessage("Version 1.2.3 would create a gap in semver. Allowed versions are 1.1.2, 1.2.0, 2.0.0");

        // assert
        verify(restClient).setCredentials(new Credentials("username", "password"));
    }

    @Test
    void emptyVersion() {
        // arrange
        mojo.setVersion("");

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(restClient, bitbucketApi, log))
            .isInstanceOf(MojoFailureException.class)
            .hasMessage("version cannot be empty");
    }

    @Test
    void snapshotVersion() throws IOException, MojoFailureException, MojoExecutionException {
        // arrange
        mojo.setVersion("1.2.3-SNAPSHOT");

        // act
        mojo.doExecute(restClient, bitbucketApi, log);

        // assert
        verify(log).info("Skipped check for snapshot version 1.2.3-SNAPSHOT");
        verify(restClient, never()).setCredentials(any());
        verify(bitbucketApi, never()).tagExists(anyString(), anyString(), anyString());
        verify(bitbucketApi, never()).tagOfBiggestVersion(anyString(), anyString());
    }
}
