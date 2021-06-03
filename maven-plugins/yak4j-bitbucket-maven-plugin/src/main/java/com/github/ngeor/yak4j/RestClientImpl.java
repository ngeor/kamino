package com.github.ngeor.yak4j;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Implementation of {@link RestClient} using {@link OkHttpClient}.
 */
public class RestClientImpl implements RestClient {
    private Credentials credentials;

    @Override
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", basicAuthHeader())
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalStateException(String.format("Request for url %s failed: %d", url, response.code()));
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IllegalStateException("Got null response body");
            }

            String responseAsString = body.string();
            if (responseAsString == null) {
                throw new IllegalStateException("Got null response body as string");
            }

            return responseAsString;
        }
    }

    @Override
    public void post(String url, String body) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            body
        );

        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", basicAuthHeader())
            .post(requestBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IllegalStateException("Request failed: " + response.code());
            }
        }
    }

    private String basicAuthHeader() {
        return okhttp3.Credentials.basic(credentials.getUsername(), credentials.getPassword());
    }
}
