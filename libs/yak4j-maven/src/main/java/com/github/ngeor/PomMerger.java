package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Set;
import org.w3c.dom.Node;

// Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
public final class PomMerger {
    public final class Parent {
        private final DocumentWrapper parent;

        public Parent(DocumentWrapper parent) {
            this.parent = parent;
        }

        public DocumentWrapper mergeChild(DocumentWrapper child) {
            return merge(parent, child);
        }
    }

    public Parent withParent(DocumentWrapper parent) {
        return new Parent(parent);
    }

    /**
     * Merges the parent pom into the child.
     * @param parentPom The parent pom (should be resolved)
     * @param childPom The child pom
     */
    private DocumentWrapper merge(DocumentWrapper parentPom, DocumentWrapper childPom) {
        mergeProject(parentPom.getDocumentElement(), childPom.getDocumentElement());
        return childPom;
    }

    private void mergeProject(ElementWrapper parentPomProject, ElementWrapper childPomProject) {
        parentPomProject.getChildElements().forEach(e -> mergeProjectChildElement(e, childPomProject));
    }

    private void mergeProjectChildElement(ElementWrapper parentPomProjectChild, ElementWrapper childPomProject) {
        String name = parentPomProjectChild.getNodeName();
        if ("properties".equals(name)) {
            mergeProperties(parentPomProjectChild, childPomProject);
        } else if (Set.of("modelVersion", "groupId", "version", "packaging", "description", "url", "scm")
                .contains(name)) {
            mergeSingleOccurringPlainTextElementRecursively(parentPomProjectChild, childPomProject);
        } else if (Set.of("artifactId", "name").contains(name)) {
            // Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
            // ignore
        } else if (Set.of("licenses", "developers", "distributionManagement").contains(name)) {
            mergeDeepImport(parentPomProjectChild, childPomProject);
        } else {
            throw new UnsupportedOperationException(String.format("Not implemented merging %s", name));
        }
    }

    private void mergeDeepImport(ElementWrapper parentPomChildElement, ElementWrapper childPomElement) {
        String name = parentPomChildElement.getNodeName();
        if (childPomElement.firstElement(name).isPresent()) {
            throw new UnsupportedOperationException(String.format("Cannot merge %s when it already exists", name));
        }

        ElementWrapper childPomChildElement = childPomElement.ensureChild(name);
        for (var it = parentPomChildElement.getChildNodesAsIterator(); it.hasNext(); ) {
            Node node = it.next();
            childPomChildElement.appendChild(childPomChildElement.importNode(node, true));
        }
    }

    private void mergeSingleOccurringPlainTextElementRecursively(
            ElementWrapper parentPomChildElement, ElementWrapper childPomElement) {
        String name = parentPomChildElement.getNodeName();
        if (parentPomChildElement.hasChildElements()) {
            ElementWrapper targetChildElement = childPomElement.ensureChild(name);
            parentPomChildElement
                    .getChildElements()
                    .forEach(x -> mergeSingleOccurringPlainTextElementRecursively(x, targetChildElement));
        } else {
            parentPomChildElement.getTextContentOptional().ifPresent(text -> {
                childPomElement.appendIfMissing(name).ifPresent(newChildElement -> newChildElement.setTextContent(text));
            });
        }
    }

    private void mergeProperties(ElementWrapper sourceProperties, ElementWrapper target) {
        sourceProperties.getChildElements().forEach(p -> {
            ElementWrapper childProperties = target.ensureChild("properties");

            if (childProperties.findChildElements(p.getNodeName()).findAny().isPresent()) {
                // child property already exists, so it wins
            } else {
                childProperties.ensureChildText(p.getNodeName(), p.getTextContent());
            }
        });
    }
}
