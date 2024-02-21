package com.github.ngeor.yak4jdom;

import java.util.List;

public record TypedMavenDocument(
        String groupId,
        String artifactId,
        String name,
        String description,
        String modelVersion,
        String packaging,
        List<String> modules,
        List<License> licenses) {
    record License(String name, String url) {}
}
