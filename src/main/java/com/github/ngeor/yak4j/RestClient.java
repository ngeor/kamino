package com.github.ngeor.yak4j;

import java.io.IOException;

/**
 * An abstraction for a REST client.
 */
public interface RestClient {
    void setCredentials(Credentials credentials);

    String get(String url) throws IOException;

    void post(String url, String body) throws IOException;
}
