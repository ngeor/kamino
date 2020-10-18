package com.github.ngeor.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

/**
 * A simple http client.
 */
public class HttpClientImpl implements HttpClient {

  private static final int STATUS_OK = 200;

  private String authorizationHeader;

  /**
   * Creates an instance of this class.
   * @param username The username for basic authentication.
   * @param password The password for basic authentication.
   */
  public HttpClientImpl(String username, String password) {
    this.authorizationHeader =
        "Basic " +
        Base64.getEncoder().encodeToString(
            (username + ":" + password).getBytes(StandardCharsets.ISO_8859_1));
  }

  @Override
  public InputStream read(String url) {
    try {
      URLConnection urlConnection = new URL(url).openConnection();
      HttpsURLConnection httpsURLConnection = (HttpsURLConnection)urlConnection;
      httpsURLConnection.setRequestMethod("GET");
      httpsURLConnection.setDoOutput(true);
      httpsURLConnection.setRequestProperty("Authorization",
                                            authorizationHeader);
      httpsURLConnection.connect();
      int responseCode = httpsURLConnection.getResponseCode();

      if (responseCode != STATUS_OK) {
        String response = httpsURLConnection.getResponseMessage();
        throw new IOException(String.format(
            "Request failed with code %d and response %s for url %s",
            responseCode, response, url));
      }

      return httpsURLConnection.getInputStream();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
