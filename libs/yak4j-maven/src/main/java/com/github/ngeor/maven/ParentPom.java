package com.github.ngeor.maven;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Optional;

public record ParentPom(MavenCoordinates coordinates, String relativePath) {
    public static Optional<ParentPom> fromDocument(DocumentWrapper document) {
        return Optional.ofNullable(document).map(DocumentWrapper::getDocumentElement).stream()
                .flatMap(ElementWrapper::getChildElements)
                .filter(e -> "parent".equals(e.getNodeName()))
                .map(ParentPom::fromParentElement)
                .findFirst();
    }

    private static ParentPom fromParentElement(ElementWrapper parentElement) {
        String groupId = null;
        String artifactId = null;
        String version = null;
        String relativePath = null;

        // break the loop if all elements have been found
        for (var it = parentElement.getChildElementsAsIterator();
                it.hasNext() && (groupId == null || artifactId == null || version == null || relativePath == null); ) {
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
                case "relativePath":
                    relativePath = node.getTextContentTrimmed().orElse(null);
                    break;
                default:
                    break;
            }
        }

        return new ParentPom(new MavenCoordinates(groupId, artifactId, version), relativePath);
    }
}
