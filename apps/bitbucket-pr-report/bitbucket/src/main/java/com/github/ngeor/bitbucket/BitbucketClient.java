package com.github.ngeor.bitbucket;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

import com.github.ngeor.bitbucket.models.PaginatedPipelines;
import com.github.ngeor.bitbucket.models.PaginatedPullRequests;
import com.github.ngeor.bitbucket.models.PaginatedRepositories;
import com.github.ngeor.bitbucket.models.Pipeline;
import com.github.ngeor.bitbucket.models.PullRequest;
import com.github.ngeor.bitbucket.models.Repository;
import com.github.ngeor.http.JsonHttpClient;

/**
 * A Bitbucket client.
 */
public class BitbucketClient {
  private final JsonHttpClient jsonHttpClient;
  private String owner;
  private String baseUrl = "https://api.bitbucket.org/2.0/";

  public BitbucketClient(JsonHttpClient jsonHttpClient) {
    this.jsonHttpClient = jsonHttpClient;
  }

  public String getBaseUrl() { return baseUrl; }

  public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

  public String getOwner() { return owner; }

  public void setOwner(String owner) { this.owner = owner; }

  /**
   * Gets all repositories.
   */
  public Stream<Repository> getAllRepositories() {
      Iterable<PaginatedRepositories> iterable =
        PageCollector.collectAll(new Paginator<PaginatedRepositories>() {
          @Override
          public PaginatedRepositories first() {
            String url = baseUrl + "repositories/" + owner;
            return jsonHttpClient.read(url, PaginatedRepositories.class);
          }

          @Override
          public PaginatedRepositories next(
              PaginatedRepositories previousPage) {
            String nextUrl = previousPage.getNext();
            if (StringUtils.isBlank(nextUrl)) {
              return null;
            }

            return jsonHttpClient.read(nextUrl, PaginatedRepositories.class);
          }
        });
    return StreamSupport.stream(iterable.spliterator(), false)
        .flatMap(r -> r.getValues().stream());
  }

  /**
   * Gets all pipelines.
   */
  public Stream<Pipeline> getAllPipelines(String slug) {
      Iterable<PaginatedPipelines> iterable =
        PageCollector.collectAll(new Paginator<PaginatedPipelines>() {
          @Override
          public PaginatedPipelines first() {
            String url = baseUrl + "repositories/" + owner + "/" + slug +
                         "/pipelines/?sort=-created_on";
            return jsonHttpClient.read(url, PaginatedPipelines.class);
          }

          @Override
          public PaginatedPipelines next(PaginatedPipelines previousPage) {
            if (previousPage.getValues().isEmpty()) {
              return null;
            }

            String nextUrl = String.format(
                "%srepositories/%s/%s/pipelines/?page=%d&sort=-created_on",
                baseUrl, owner, slug, previousPage.getPage() + 1);

            return jsonHttpClient.read(nextUrl, PaginatedPipelines.class);
          }
        });
    return StreamSupport.stream(iterable.spliterator(), false)
        .flatMap(r -> r.getValues().stream());
  }

  public PullRequest getPrDetails(PullRequest pr) {
    String url = pr.getLinks().getSelf().getHref();
    return jsonHttpClient.read(url, PullRequest.class);
  }

  /**
   * Gets all merged pull requests.
   */
  public Stream<PullRequest> getAllMergedPullRequests(String slug) {
      Iterable<PaginatedPullRequests> iterable = PageCollector.collectAll(new Paginator<
                                            PaginatedPullRequests>() {
      @Override
      public PaginatedPullRequests first() {
        String url = String.format(
            "%srepositories/%s/%s/pullrequests?state=MERGED&sort=-created_on",
            baseUrl, owner, slug);
        return jsonHttpClient.read(url, PaginatedPullRequests.class);
      }

      @Override
      public PaginatedPullRequests next(PaginatedPullRequests previousPage) {
        String nextUrl = previousPage.getNext();
        if (StringUtils.isBlank(nextUrl)) {
          return null;
        }

        return jsonHttpClient.read(nextUrl, PaginatedPullRequests.class);
      }
    });
    return StreamSupport.stream(iterable.spliterator(), false)
        .flatMap(r -> r.getValues().stream())
        .map(this ::getPrDetails);
  }
}
