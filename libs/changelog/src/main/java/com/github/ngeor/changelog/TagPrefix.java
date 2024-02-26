package com.github.ngeor.changelog;

import com.github.ngeor.git.Tag;
import com.github.ngeor.versions.SemVer;
import org.apache.commons.lang3.Validate;

public record TagPrefix(String tagPrefix) {
    public TagPrefix {
        Validate.notBlank(tagPrefix);
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

    public boolean tagStartsWithExpectedPrefix(String tag) {
        return tag != null && tag.startsWith(tagPrefix);
    }
}
