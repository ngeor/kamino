package com.github.ngeor.maven;

import com.github.ngeor.yak4jdom.ElementWrapper;

public record MavenCoordinates(String groupId, String artifactId, String version) {
    public MavenCoordinates {
        if (artifactId == null || artifactId.isBlank()) {
            throw new IllegalArgumentException("artifactId can never be null or blank, as it cannot be inherited");
        }
    }

    public static MavenCoordinates fromElement(ElementWrapper element) {
        if (element == null) {
            throw new IllegalArgumentException("element cannot be null");
        }

        String groupId = null;
        String artifactId = null;
        String version = null;

        // break the loop if all elements have been found
        for (var it = element.getChildElementsAsIterator();
                it.hasNext() && (groupId == null || artifactId == null || version == null); ) {
            var node = it.next();
            switch (node.getNodeName()) {
                case "groupId":
                    groupId = node.getTextContentTrimmed().orElse(null);
                    break;
                case "artifactId":
                    artifactId = node.getTextContentTrimmed().orElse(null);
                    break;
                case "version":
                    version = node.getTextContentTrimmed().orElse(null);
                    break;
                default:
                    break;
            }
        }

        return new MavenCoordinates(groupId, artifactId, version);
    }

    public MavenCoordinates requireAllFields() {
        // artifactId is checked at the constructor

        if (groupId == null || groupId.isBlank()) {
            throw new IllegalStateException(String.format("groupId is missing (artifactId=%s)", artifactId));
        }

        if (version == null || version.isBlank()) {
            throw new IllegalStateException(String.format("version is missing %s:%s", groupId, artifactId));
        }

        return this;
    }

    public MavenCoordinates withVersion(String newVersion) {
        return new MavenCoordinates(groupId, artifactId, newVersion);
    }
}
