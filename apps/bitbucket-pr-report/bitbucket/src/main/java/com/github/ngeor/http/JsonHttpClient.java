package com.github.ngeor.http;

/**
 * An HTTP client that deserializes from json.
 */
public interface JsonHttpClient {
    /**
     * Performs a GET operation on the given URL and deserializes
     * the JSON response.
     *
     * @param url          The URL.
     * @param responseType The class of the response.
     * @param <E>          The type of the response.
     * @return The deserialized result.
     */
    <E> E read(String url, Class<E> responseType);
}
