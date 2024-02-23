package com.github.ngeor.mr;

import com.github.ngeor.git.Tag;
import com.github.ngeor.versions.SemVer;

public final class TagPrefix {
    private final String tagPrefix;

    private TagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public static TagPrefix forPath(String path) {
        String tagPrefix;
        if (path == null || path.isBlank()) {
            tagPrefix = "v";
        } else {
            if (path.endsWith("/") || path.endsWith("\\")) {
                throw new IllegalArgumentException("path must not end with path separator");
            }
            tagPrefix = path + "/v";
        }
        return new TagPrefix(tagPrefix);
    }

    public String tagPrefix() {
        return tagPrefix;
    }

    public String addTagPrefix(SemVer version) {
        if (version == null) {
            throw new IllegalArgumentException("version cannot be null");
        }
        return tagPrefix() + version;
    }

    public SemVer stripTagPrefix(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag cannot be null");
        }
        String name = tag.name();
        if (name == null) {
            throw new IllegalArgumentException("tag name cannot be null");
        }
        String prefix = tagPrefix();
        if (!name.startsWith(prefix)) {
            throw new IllegalArgumentException(String.format("tag %s does not start with prefix %s", name, prefix));
        }
        return SemVer.parse(name.substring(prefix.length()));
    }

    public String stripTagPrefixIfPresent(String input) {
        String prefix = tagPrefix();
        if (input.startsWith(prefix)) {
            return input.substring(prefix.length());
        }
        return input;
    }
}
