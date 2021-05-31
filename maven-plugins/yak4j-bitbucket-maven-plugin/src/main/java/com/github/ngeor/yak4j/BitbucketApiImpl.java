package com.github.ngeor.yak4j;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the Bitbucket Cloud REST API.
 */
class BitbucketApiImpl implements BitbucketApi {
    private static final String API_BASE = "https://api.bitbucket.org/2.0";
    private static final String REPOSITORIES_URL = API_BASE + "/repositories";

    private final RestClient restClient;

    /**
     * Creates an instance of this class.
     * @param restClient The REST client.
     */
    BitbucketApiImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean tagExists(String owner, String slug, String tag) throws IOException {
        String url = tagsUrl(owner, slug) + "?q=name+%3D+%22" + tag + "%22";
        String responseAsString = restClient.get(url);
        return responseAsString.contains(tag);
    }

    @Override
    public String tagOfBiggestVersion(String owner, String slug) throws IOException {
        String url = tagsUrl(owner, slug) + "?sort=-name";
        String responseAsString = restClient.get(url);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        BitbucketTags tagsResponse = objectMapper.readValue(responseAsString, BitbucketTags.class);
        if (tagsResponse == null) {
            return null;
        }

        BitbucketTag[] values = tagsResponse.getValues();
        if (values == null || values.length <= 0) {
            return null;
        }

        return values[0].getName();
    }

    @Override
    public void createTag(String owner, String slug, String tag, String hash) throws IOException {
        String url = tagsUrl(owner, slug);
        String body = "{ \"name\" : \"" + tag + "\", \"target\" : { \"hash\" : \"" + hash + "\" } }";

        restClient.post(url, body);
    }

    private static String tagsUrl(String owner, String slug) {
        return REPOSITORIES_URL + "/" + owner + "/" + slug + "/refs/tags";
    }
}
