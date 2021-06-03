package com.github.ngeor.bitbucket;

import com.github.ngeor.bitbucket.models.Link;
import com.github.ngeor.bitbucket.models.Links;
import com.github.ngeor.bitbucket.models.PaginatedPipelines;
import com.github.ngeor.bitbucket.models.PaginatedPullRequests;
import com.github.ngeor.bitbucket.models.PaginatedRepositories;
import com.github.ngeor.bitbucket.models.Pipeline;
import com.github.ngeor.bitbucket.models.PullRequest;
import com.github.ngeor.bitbucket.models.Repository;
import com.github.ngeor.http.JsonHttpClient;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link BitbucketClient}.
 */
@ExtendWith(MockitoExtension.class)
class BitbucketClientTest {
    @InjectMocks
    private BitbucketClient bitbucketClient;

    @Mock
    private JsonHttpClient jsonHttpClient;

    @Test
    void getAllRepositories() {
        // arrange
        Repository repo1 = mock(Repository.class);
        Repository repo2 = mock(Repository.class);
        Repository repo3 = mock(Repository.class);

        PaginatedRepositories page1 =
            (PaginatedRepositories) new PaginatedRepositories()
                .addValuesItem(repo1)
                .addValuesItem(repo2)
                .next("http://page2");

        PaginatedRepositories page2 =
            new PaginatedRepositories().addValuesItem(repo3);

        doReturn(page1)
            .when(jsonHttpClient)
            .read("https://api.bitbucket.org/2.0/repositories/acme",
                PaginatedRepositories.class);
        doReturn(page2)
            .when(jsonHttpClient)
            .read("http://page2", PaginatedRepositories.class);

        bitbucketClient.setOwner("acme");

        // act and assert
        assertThat(bitbucketClient.getAllRepositories())
            .containsExactly(repo1, repo2, repo3);
    }

    @Test
    void getAllPipelines() {
        // arrange
        Pipeline pipeline1 = mock(Pipeline.class);
        Pipeline pipeline2 = mock(Pipeline.class);
        Pipeline pipeline3 = mock(Pipeline.class);

        PaginatedPipelines page1 = (PaginatedPipelines) new PaginatedPipelines()
            .addValuesItem(pipeline1)
            .addValuesItem(pipeline2)
            .page(1);

        PaginatedPipelines page2 = (PaginatedPipelines) new PaginatedPipelines()
            .addValuesItem(pipeline3)
            .page(2);

        PaginatedPipelines page3 =
            new PaginatedPipelines().values(Collections.emptyList());

        doReturn(page1)
            .when(jsonHttpClient)
            .read(
                "https://api.bitbucket.org/2.0/repositories/acme/foo/pipelines/?sort=-created_on",
                PaginatedPipelines.class);
        doReturn(page2)
            .when(jsonHttpClient)
            .read(
                "https://api.bitbucket.org/2.0/repositories/acme/foo/pipelines/?page=2&sort=-created_on",
                PaginatedPipelines.class);
        doReturn(page3)
            .when(jsonHttpClient)
            .read(
                "https://api.bitbucket.org/2.0/repositories/acme/foo/pipelines/?page=3&sort=-created_on",
                PaginatedPipelines.class);

        bitbucketClient.setOwner("acme");

        // act and assert
        assertThat(bitbucketClient.getAllPipelines("foo"))
            .containsExactly(pipeline1, pipeline2, pipeline3);
    }

    @Test
    void getAllMergedPullRequests() {
        // arrange
        PullRequest pr1Simple = new PullRequest().links(
            new Links().self(new Link().href("http://pr1")));
        PullRequest pr2Simple = new PullRequest().links(
            new Links().self(new Link().href("http://pr2")));
        PullRequest pr3Simple = new PullRequest().links(
            new Links().self(new Link().href("http://pr3")));

        PullRequest pr1Full = mock(PullRequest.class);
        PullRequest pr2Full = mock(PullRequest.class);
        PullRequest pr3Full = mock(PullRequest.class);

        PaginatedPullRequests page1 =
            (PaginatedPullRequests) new PaginatedPullRequests()
                .addValuesItem(pr1Simple)
                .addValuesItem(pr2Simple)
                .next("http://page2");

        PaginatedPullRequests page2 =
            new PaginatedPullRequests().addValuesItem(pr3Simple);

        doReturn(page1)
            .when(jsonHttpClient)
            .read(
                "https://api.bitbucket.org/2.0/repositories/acme/foo/pullrequests?state=MERGED&sort=-created_on",
                PaginatedPullRequests.class);
        doReturn(page2)
            .when(jsonHttpClient)
            .read("http://page2", PaginatedPullRequests.class);
        doReturn(pr1Full)
            .when(jsonHttpClient)
            .read("http://pr1", PullRequest.class);
        doReturn(pr2Full)
            .when(jsonHttpClient)
            .read("http://pr2", PullRequest.class);
        doReturn(pr3Full)
            .when(jsonHttpClient)
            .read("http://pr3", PullRequest.class);

        bitbucketClient.setOwner("acme");

        // act and assert
        assertThat(bitbucketClient.getAllMergedPullRequests("foo"))
            .containsExactly(pr1Full, pr2Full, pr3Full);
    }
}
