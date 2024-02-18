package com.github.ngeor.versions;

import java.util.Objects;

public class SemVer implements Comparable<SemVer> {
    private final int major;
    private final int minor;
    private final int patch;
    private final String preRelease;

    public SemVer(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = null;
    }

    public SemVer(int major, int minor, int patch, String preRelease) {
        if (preRelease == null || preRelease.isBlank()) {
            throw new IllegalArgumentException("preRelease must not be empty");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
    }

    public static SemVer parse(String s) {
        String[] parts = s.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        String[] patchParts = parts[2].split("-", 2);
        int patch = Integer.parseInt(patchParts[0]);
        if (patchParts.length == 2) {
            return new SemVer(major, minor, patch, patchParts[1]);
        }
        return new SemVer(major, minor, patch);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SemVer) {
            return compareTo((SemVer) o) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease);
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
        if (major < other.major) {
            return -1;
        } else if (major > other.major) {
            return 1;
        } else if (minor < other.minor) {
            return -1;
        } else if (minor > other.minor) {
            return 1;
        } else if (patch < other.patch) {
            return -1;
        } else if (patch > other.patch) {
            return 1;
        } else {
            if (preRelease != null) {
                if (other.preRelease != null) {
                    return preRelease.compareTo(other.preRelease);
                } else {
                    return -1;
                }
            } else {
                if (other.preRelease != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    public boolean isPreRelease() {
        return preRelease != null;
    }

    public SemVer bump(SemVerBump bump) {
        switch (bump) {
            case MAJOR:
                return new SemVer(major + 1, 0, 0);
            case MINOR:
                return new SemVer(major, minor + 1, 0);
            case PATCH:
                return new SemVer(major, minor, patch + 1);
            default:
                throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("checkstyle:HiddenField")
    public SemVer preRelease(String preRelease) {
        return new SemVer(major, minor, patch, preRelease);
    }
}
