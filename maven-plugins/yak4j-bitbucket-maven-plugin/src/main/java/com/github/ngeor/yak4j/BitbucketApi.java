package com.github.ngeor.yak4j;

import java.io.IOException;

/**
 * Bitbucket API.
 */
public interface BitbucketApi {
    /**
     * Checks if a Bitbucket Cloud repository contains the given tag.
     *
     * @param owner The owner of the repository.
     * @param slug  The slug of the repository.
     * @param tag   The tag to find.
     * @return true if the tag exists, false otherwise.
     * @throws IOException if a network error occurs.
     */
    boolean tagExists(String owner, String slug, String tag) throws IOException;

    /**
     * Gets the tag that represents the biggest version.
     *
     * @param owner The owner of the repository.
     * @param slug  The slug of the repository.
     * @return The tag name of the biggest version.
     * @throws IOException if a network error occurs.
     */
    String tagOfBiggestVersion(String owner, String slug) throws IOException;

    /**
     * Creates a new git tag in a Bitbucket repository.
     *
     * @param owner The owner of the repository.
     * @param slug  The slug of the repository.
     * @param tag   The tag.
     * @param hash  The git hash that the tag will point to.
     * @throws IOException if a network error occurs.
     */
    void createTag(String owner, String slug, String tag, String hash) throws IOException;
}
