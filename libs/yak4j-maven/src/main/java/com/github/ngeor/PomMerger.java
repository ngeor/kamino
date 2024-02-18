package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Set;

// Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
public final class PomMerger {
    public final class Parent {
        private final DocumentWrapper parent;

        public Parent(DocumentWrapper parent) {
            this.parent = parent;
        }

        public DocumentWrapper mergeChild(DocumentWrapper child) {
            return mergeIntoLeft(parent, child);
        }
    }

    public Parent withParent(DocumentWrapper parent) {
        return new Parent(parent);
    }

    /**
     * Merges the child pom into the parent.
     * @param left The parent pom (should be resolved)
     * @param right The child pom
     */
    private DocumentWrapper mergeIntoLeft(DocumentWrapper left, DocumentWrapper right) {
        // Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
        left.getDocumentElement().removeChildNodesByName("artifactId");
        left.getDocumentElement().removeChildNodesByName("name");
        right.getDocumentElement().getChildElements().forEach(e -> mergeIntoLeft(left, e));
        return left;
    }

    private void mergeIntoLeft(DocumentWrapper left, ElementWrapper right) {
        String name = right.getNodeName();
        if ("properties".equals(name)) {
            mergePropertiesIntoLeft(left, right);
        } else if (Set.of("modelVersion", "groupId", "artifactId", "version", "name", "packaging", "description", "url", "scm")
            .contains(name)) {
            mergeRecursivelyIntoLeft(left.getDocumentElement().ensureChild(name), right);
        } else {
            throw new UnsupportedOperationException(String.format("Merging %s is not implemented", name));
        }
    }

    private void mergePropertiesIntoLeft(DocumentWrapper left, ElementWrapper right) {
        right.getChildElements().forEach(e -> mergePropertyIntoLeft(left, e));
    }

    private void mergePropertyIntoLeft(DocumentWrapper left, ElementWrapper right) {
        left.getDocumentElement().ensureChild("properties").ensureChildText(right);
    }

    private void mergeRecursivelyIntoLeft(ElementWrapper left, ElementWrapper right) {
        if (right.hasChildElements()) {
            right.getChildElements().forEach(e -> mergeRecursivelyIntoLeft(left.ensureChild(e.getNodeName()), e));
        } else {
            left.setTextContent(right.getTextContent());
        }
    }
}
