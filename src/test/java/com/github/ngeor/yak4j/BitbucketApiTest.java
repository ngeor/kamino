package com.github.ngeor.yak4j;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BitbucketApiImpl}.
 */
class BitbucketApiTest {
    @Mock
    private RestClient restClient;

    private BitbucketApiImpl api;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        api = new BitbucketApiImpl(restClient);
    }

    @Test
    void tagExists() throws IOException {
        // arrange
        when(
            restClient.get(
                "https://api.bitbucket.org/2.0/repositories/owner/slug/refs/tags?q=name+%3D+%22v1.0.0%22"
                )
        ).thenReturn("v1.0.0");

        // assert
        boolean result = api.tagExists("owner", "slug", "v1.0.0");

        // act
        assertThat(result).isTrue();
    }

    @Test
    void tagDoesNotExist() throws IOException {
        // arrange
        when(
            restClient.get(
                "https://api.bitbucket.org/2.0/repositories/owner/slug/refs/tags?q=name+%3D+%22v1.0.0%22"
            )
        ).thenReturn("");

        // assert
        boolean result = api.tagExists("owner", "slug", "v1.0.0");

        // act
        assertThat(result).isFalse();
    }

    @Test
    void tagOfBiggestVersion() throws IOException {
        // arrange
        when(
            restClient.get(
                "https://api.bitbucket.org/2.0/repositories/owner/slug/refs/tags?sort=-name"
            )
        ).thenReturn("{\"pagelen\": 10, \"values\": [{\"name\": \"v1.4.0\"}]}");

        // assert
        String biggestVersion = api.tagOfBiggestVersion("owner", "slug");

        // act
        assertThat(biggestVersion).isEqualTo("v1.4.0");
    }

    @Test
    void tagOfBiggestVersionNoTags() throws IOException {
        // arrange
        when(
            restClient.get(
                "https://api.bitbucket.org/2.0/repositories/owner/slug/refs/tags?sort=-name"
            )
        ).thenReturn("{\"pagelen\": 10, \"values\": []}");

        // assert
        String biggestVersion = api.tagOfBiggestVersion("owner", "slug");

        // act
        assertThat(biggestVersion).isNullOrEmpty();
    }

    @Test
    void createTag() throws IOException {
        // act
        api.createTag("owner", "slug", "v1.2.3", "abc123");

        // assert
        verify(restClient).post(
            "https://api.bitbucket.org/2.0/repositories/owner/slug/refs/tags",
            "{ \"name\" : \"v1.2.3\", \"target\" : { \"hash\" : \"abc123\" } }"
        );
    }
}
