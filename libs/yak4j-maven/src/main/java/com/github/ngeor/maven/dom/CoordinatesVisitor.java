package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import java.util.function.BiConsumer;

public class CoordinatesVisitor {
    private final BiConsumer<ElementWrapper, MavenCoordinates> consumer;
    private final boolean onlyAllFields;

    public CoordinatesVisitor(boolean onlyAllFields, BiConsumer<ElementWrapper, MavenCoordinates> consumer) {
        this.onlyAllFields = onlyAllFields;
        this.consumer = Objects.requireNonNull(consumer);
    }

    public CoordinatesVisitor(BiConsumer<ElementWrapper, MavenCoordinates> consumer) {
        this(true, consumer);
    }

    public void visit(DocumentWrapper document) {
        visit(document.getDocumentElement());
    }

    private void visit(ElementWrapper element) {
        String groupId = null;
        String artifactId = null;
        String version = null;
        int flags = 0;
        for (ElementWrapper child : element.getChildElementsAsIterable()) {
            String nodeName = child.getNodeName();
            switch (nodeName) {
                case ElementNames.GROUP_ID:
                    groupId = child.getTextContentTrimmed().orElse(null);
                    flags |= 1;
                    break;
                case ElementNames.ARTIFACT_ID:
                    artifactId = child.getTextContentTrimmed().orElse(null);
                    flags |= 2;
                    break;
                case ElementNames.VERSION:
                    version = child.getTextContentTrimmed().orElse(null);
                    flags |= 4;
                    break;
                default:
                    visit(child);
                    break;
            }
        }

        if ((onlyAllFields && flags == 7) || (!onlyAllFields && flags > 0)) {
            consumer.accept(element, new MavenCoordinates(groupId, artifactId, version));
        }
    }
}
