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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link PublishTagMojo}.
 */
class PublishTagMojoTest {
    private PublishTagMojo mojo;

    @Mock
    private BitbucketApi bitbucketApi;

    @Mock
    private RestClient restClient;

    @Mock
    private Log log;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        mojo = new PublishTagMojo();
        mojo.setVersion("1.2.3");
        mojo.setUsername("username");
        mojo.setPassword("password");
        mojo.setOwner("owner");
        mojo.setSlug("slug");
        mojo.setHash("hash");
    }

    @Test
    void doExecute() throws MojoFailureException, MojoExecutionException, IOException {
        // act
        mojo.doExecute(restClient, bitbucketApi, log);

        // assert
        verify(log).info("Published tag v1.2.3");
        verify(bitbucketApi).createTag("owner", "slug", "v1.2.3", "hash");
    }

    @Test
    void emptyVersion() throws IOException {
        // arrange
        mojo.setVersion("");

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(restClient, bitbucketApi, log))
            .isInstanceOf(MojoFailureException.class)
            .hasMessage("version cannot be empty");

        verify(bitbucketApi, never()).createTag(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void snapshotVersion() throws IOException {
        // arrange
        mojo.setVersion("1.2.3-SNAPSHOT");

        // act and assert
        assertThatThrownBy(() -> mojo.doExecute(restClient, bitbucketApi, log))
            .isInstanceOf(MojoFailureException.class)
            .hasMessage("Cannot publish tag for snapshot version 1.2.3-SNAPSHOT");

        verify(bitbucketApi, never()).createTag(anyString(), anyString(), anyString(), anyString());
    }
}
