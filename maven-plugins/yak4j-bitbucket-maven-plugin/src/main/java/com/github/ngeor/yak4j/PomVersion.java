package com.github.ngeor.yak4j;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a version defined in pom.xml.
 */
public class PomVersion {
    private final int major;
    private final int minor;
    private final int patch;
    private final boolean snapshot;

    /**
     * Creates a new instance of this class.
     *
     * @param version The version defined in pom.xml.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public PomVersion(String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("version cannot be empty");
        }

        Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)(-SNAPSHOT)?");
        Matcher matcher = pattern.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("invalid version %s", version));
        }

        major = Integer.parseInt(matcher.group(1));
        minor = Integer.parseInt(matcher.group(2));
        patch = Integer.parseInt(matcher.group(3));
        snapshot = matcher.group(4) != null;
    }

    /**
     * Creates an instance of this class.
     * @param major The major version component.
     * @param minor The minor version component.
     * @param patch The patch version component.
     * @param snapshot Determines if it is a snapshot version.
     */
    public PomVersion(int major, int minor, int patch, boolean snapshot) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    /**
     * Gets the allowed next versions after the current version.
     *
     * @return The allowed next versions.
     */
    public List<String> allowedVersions() {
        return Stream.of(
            nextPatch(),
            nextMinor(),
            nextMajor()
        ).map(PomVersion::toString).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PomVersion)) {
            return false;
        }

        PomVersion that = (PomVersion) o;
        return major == that.major
            && minor == that.minor
            && patch == that.patch
            && snapshot == that.snapshot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, snapshot);
    }

    @Override
    public String toString() {
        String version = String.format("%d.%d.%d", major, minor, patch);
        if (snapshot) {
            return version + "-SNAPSHOT";
        }

        return version;
    }

    /**
     * Checks if this version is an allowed next version of the given version.
     * @param smallerVersion The version that is an allowed previous version of this instance.
     * @return true if this instance is an allowed next version of the given instance, false otherwise.
     */
    public boolean isAllowedNextVersionOf(PomVersion smallerVersion) {
        return this.equals(smallerVersion.nextMajor())
            || this.equals(smallerVersion.nextMinor())
            || this.equals(smallerVersion.nextPatch());
    }

    private PomVersion nextPatch() {
        return new PomVersion(major, minor, patch + 1, false);
    }

    private PomVersion nextMinor() {
        return new PomVersion(major, minor + 1, 0, false);
    }

    private PomVersion nextMajor() {
        return new PomVersion(major + 1, 0, 0, false);
    }
}
