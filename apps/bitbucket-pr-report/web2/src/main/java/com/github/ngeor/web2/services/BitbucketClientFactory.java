package com.github.ngeor.web2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ngeor.bitbucket.BitbucketClient;
import com.github.ngeor.http.HttpClientImpl;
import com.github.ngeor.http.JsonHttpClientImpl;
import com.github.ngeor.web2.db.CredentialsRepository;

/**
 * Configuration for the Bitbucket client.
 */
@Service
public class BitbucketClientFactory {
  @Autowired private ObjectMapper objectMapper;

  @Autowired private CredentialsRepository credentialsRepository;

  /**
   * Gets the Bitbucket client.
   */
  public BitbucketClient bitbucketClient(String owner) {
    return credentialsRepository.findById(owner)
        .map(credentials -> {
          String username = credentials.getUsername();
          String password = credentials.getPassword();
          BitbucketClient result =
              new BitbucketClient(JsonHttpClientImpl.create(
                  new HttpClientImpl(username, password), objectMapper));
          result.setOwner(owner);
          return result;
        })
        .orElseThrow(
            ()
                -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No credentials for owner " + owner));
  }
}
