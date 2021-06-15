package com.github.ngeor.yak4jcli;

/**
 * Describes the different ways a version can be bumped using semantic versioning.
 */
public enum SemVerBump {
    /**
     * Major version bump.
     */
    MAJOR,

    /**
     * Minor version bump.
     */
    MINOR,

    /**
     * Patch version bump.
     */
    PATCH
}
