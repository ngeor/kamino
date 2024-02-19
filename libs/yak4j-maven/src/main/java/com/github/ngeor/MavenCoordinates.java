package com.github.ngeor;

import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Optional;

public record MavenCoordinates(String groupId, String artifactId, String version) {
    public static Optional<MavenCoordinates> fromElement(ElementWrapper element) {
        if (element == null) {
            return Optional.empty();
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

        return Optional.of(new MavenCoordinates(groupId, artifactId, version));
    }

    public MavenCoordinates requireAllFields() {
        if (groupId == null
                || groupId.isBlank()
                || artifactId == null
                || artifactId.isBlank()
                || version == null
                || version.isBlank()) {
            throw new IllegalStateException();
        }

        return this;
    }

    public MavenCoordinates withVersion(String newVersion) {
        return new MavenCoordinates(groupId, artifactId, newVersion);
    }
}
