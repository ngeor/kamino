package com.github.ngeor.versions;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SemVer(int major, int minor, int patch, String preRelease) implements Comparable<SemVer> {
    private static final Pattern SEMVER_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(-(.+))?$");

    public SemVer(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    public static SemVer parse(String input) {
        Objects.requireNonNull(input);
        return tryParse(input)
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("Cannot parse %s as a semantic version", input)));
    }

    public static Optional<SemVer> tryParse(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }

        Matcher matcher = SEMVER_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        String preRelease = matcher.group(5);
        return Optional.of(new SemVer(major, minor, patch, preRelease));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(major).append('.').append(minor).append('.').append(patch);
        if (preRelease != null) {
            result.append('-').append(preRelease);
        }
        return result.toString();
    }

    @Override
    public int compareTo(SemVer other) {
        // 1.0.0-alpha < 1.0.0
        int cmp = Integer.compare(major, other.major);
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(minor, other.minor);
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(patch, other.patch);
        if (cmp != 0) {
            return cmp;
        }
        if (isPreRelease()) {
            if (other.isPreRelease()) {
                return preRelease.compareTo(other.preRelease);
            } else {
                return -1;
            }
        } else {
            if (other.isPreRelease()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean isPreRelease() {
        return preRelease != null && !preRelease.isBlank();
    }

    public SemVer bump(SemVerBump bump) {
        Objects.requireNonNull(bump);
        if (isPreRelease()) {
            throw new IllegalArgumentException("Cannot bump a pre-release version");
        }
        return switch (bump) {
            case MAJOR -> new SemVer(major + 1, 0, 0);
            case MINOR -> new SemVer(major, minor + 1, 0);
            case PATCH -> new SemVer(major, minor, patch + 1);
        };
    }

    @SuppressWarnings("checkstyle:HiddenField")
    public SemVer preRelease(String preRelease) {
        return new SemVer(major, minor, patch, preRelease);
    }
}
