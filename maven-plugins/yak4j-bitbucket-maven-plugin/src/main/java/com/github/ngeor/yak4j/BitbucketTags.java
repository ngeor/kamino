package com.github.ngeor.yak4j;

/**
 * A response of the tags request.
 */
@SuppressWarnings("WeakerAccess")
class BitbucketTags {
    private BitbucketTag[] values;

    public BitbucketTag[] getValues() {
        return values;
    }

    public void setValues(BitbucketTag[] values) {
        this.values = values;
    }
}
