package com.github.ngeor.bitbucket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.ngeor.bitbucket.models.Repository;
import com.github.ngeor.http.HttpClient;
import com.github.ngeor.http.HttpClientImpl;
import com.github.ngeor.http.JsonHttpClient;
import com.github.ngeor.http.JsonHttpClientImpl;

/**
 * Integration test for {@link BitbucketClient}.
 */
@Disabled("needs live credentials")
class BitbucketClientIT {

  private BitbucketClient bitbucketClient;

  @BeforeEach
  void beforeEach() {
    String username = "";
    String password = "";
    String owner = "";
    HttpClient httpClient = new HttpClientImpl(username, password);
    JsonHttpClient jsonHttpClient = JsonHttpClientImpl.create(httpClient);
    bitbucketClient = new BitbucketClient(jsonHttpClient);
    bitbucketClient.setOwner(owner);
  }

  @Test
  void getAllRepositories() {
    bitbucketClient.getAllRepositories().forEach(System.out::println);
  }

  @Test
  void getAllPipelines() {
    bitbucketClient.getAllRepositories()
        .map(Repository::getSlug)
        .flatMap(bitbucketClient::getAllPipelines)
        .forEach(System.out::println);
  }

  @Test
  void getAllPullRequests() {
    bitbucketClient.getAllRepositories()
        .map(Repository::getSlug)
        .flatMap(bitbucketClient::getAllMergedPullRequests)
        .forEach(System.out::println);
  }
}
