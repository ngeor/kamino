package com.github.ngeor.http;

import java.io.InputStream;

/**
 * A simple http client.
 */
public interface HttpClient {
    /**
     * Performs a GET operation on the given URL.
     *
     * @param url The url.
     * @return The stream.
     */
    InputStream read(String url);
}
