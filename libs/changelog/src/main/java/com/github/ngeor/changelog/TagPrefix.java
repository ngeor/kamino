package com.github.ngeor.changelog;

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
