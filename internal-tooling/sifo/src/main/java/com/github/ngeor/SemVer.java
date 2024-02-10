package com.github.ngeor;

public record SemVer(int major, int minor, int patch) {
    public static SemVer parse(String version) {
        String[] parts = version.split("\\.");
        return new SemVer(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    public SemVer increasePatch() {
        return new SemVer(major, minor, patch + 1);
    }

    public SemVer increaseMinor() {
        return new SemVer(major, minor + 1, 0);
    }

    public SemVer increaseMajor() {
        return new SemVer(major + 1, 0, 0);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
