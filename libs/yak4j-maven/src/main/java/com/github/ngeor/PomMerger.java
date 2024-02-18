package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Set;
import java.util.function.BiConsumer;

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
        ElementWrapper leftDocumentElement = left.getDocumentElement();
        leftDocumentElement.removeChildNodesByName("artifactId");
        leftDocumentElement.removeChildNodesByName("name");
        mergeIntoLeft(leftDocumentElement, right.getDocumentElement(), this::mergeProjectChildIntoLeft);
        return left;
    }

    private void mergeIntoLeft(
            ElementWrapper left, ElementWrapper right, BiConsumer<ElementWrapper, ElementWrapper> consumer) {
        if (right.hasChildElements()) {
            right.getChildElements()
                    .forEach(rightChild -> consumer.accept(left.ensureChild(rightChild.getNodeName()), rightChild));
        } else {
            right.getTextContentTrimmed().ifPresent(text -> {
                if (left.hasChildElements()) {
                    throw new IllegalStateException(String.format(
                            "Cannot merge %s as a text node because the parent pom has child elements", right.path()));
                }
                left.setTextContent(text);
            });
        }
    }

    private void mergeRecursivelyIntoLeft(ElementWrapper left, ElementWrapper right) {
        mergeIntoLeft(left, right, this::mergeRecursivelyIntoLeft);
    }

    private void mergeProjectChildIntoLeft(ElementWrapper left, ElementWrapper right) {
        String name = right.getNodeName();
        if (Set.of(
                        "properties",
                        "modelVersion",
                        "groupId",
                        "artifactId",
                        "version",
                        "name",
                        "packaging",
                        "description",
                        "url",
                        "scm")
                .contains(name)) {
            mergeRecursivelyIntoLeft(left, right);
        } else if ("build".equals(name)) {
            mergeBuildIntoLeft(left, right);
        } else if ("dependencies".equals(name)) {
            mergeDependenciesInfoLeft(left, right);
        } else if ("profiles".equals(name)) {
            mergeProfilesIntoLeft(left, right);
        } else {
            throw new MergeNotImplementedException(right);
        }
    }

    private void mergeBuildIntoLeft(ElementWrapper left, ElementWrapper right) {
        mergeIntoLeft(left, right, (leftChild, rightChild) -> {
            String name = rightChild.getNodeName();
            if ("plugins".equals(name)) {
                mergeBuildPluginsIntoLeft(leftChild, rightChild);
            } else if ("extensions".equals(name)) {
                mergeBuildExtensionsIntoLeft(leftChild, rightChild);
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        });
    }

    private void mergeBuildExtensionsIntoLeft(ElementWrapper left, ElementWrapper right) {
        mergeCoordinateIntoLeft("extension", left, right);
    }

    private void mergeBuildPluginsIntoLeft(ElementWrapper left, ElementWrapper right) {
        mergeCoordinateIntoLeft("plugin", left, right);
    }

    private void mergeDependenciesInfoLeft(ElementWrapper left, ElementWrapper right) {
        mergeCoordinateIntoLeft("dependency", left, right);
    }

    private void mergeCoordinateIntoLeft(String elementName, ElementWrapper left, ElementWrapper right) {
        right.getChildElements().forEach(rightChild -> {
            String name = rightChild.getNodeName();
            if (elementName.equals(name)) {
                String groupId = requireChildText(rightChild, "groupId");
                String artifactId = requireChildText(rightChild, "artifactId");
                ElementWrapper leftChild = left.getChildElements()
                        .filter(x -> name.equals(x.getNodeName()))
                        .filter(x -> hasChildText(x, "groupId", groupId))
                        .filter(x -> hasChildText(x, "artifactId", artifactId))
                        .findFirst()
                        .orElseGet(() -> left.append(name));
                mergeRecursivelyIntoLeft(leftChild, rightChild);
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        });
    }

    private void mergeProfilesIntoLeft(ElementWrapper left, ElementWrapper right) {
        right.getChildElements().forEach(rightChild -> {
            String name = rightChild.getNodeName();
            if ("profile".equals(name)) {
                String id = requireChildText(rightChild, "id");
            } else {
                throw new MergeNotImplementedException(rightChild);
            }
        });
    }

    private static String requireChildText(ElementWrapper element, String childName) {
        return element.childTextContentsTrimmed(childName)
                .findAny()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Element %s should have child element %s", element.path(), childName)));
    }

    private static boolean hasChildText(ElementWrapper element, String childName, String text) {
        return element.childTextContentsTrimmed(childName).anyMatch(text::equals);
    }
}
