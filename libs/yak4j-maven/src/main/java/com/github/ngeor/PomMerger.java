package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import org.w3c.dom.Node;

import java.util.Set;

// Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
public class PomMerger {
    public String merge(String source, String target) {
        DocumentWrapper sourceDoc = DocumentWrapper.parseString(source);
        DocumentWrapper targetDoc = DocumentWrapper.parseString(target);
        merge(sourceDoc, targetDoc);
        targetDoc.indent();
        return targetDoc.writeToString();
    }

    /**
     * Merges the parent pom into the child.
     * @param source The parent pom (should be resolved)
     * @param target The child pom
     */
    public void merge(DocumentWrapper source, DocumentWrapper target) {
        mergeProject(source.getDocumentElement(), target.getDocumentElement());
    }

    private void mergeProject(ElementWrapper source, ElementWrapper target) {
        source.getChildElements().forEach(sourceChildElement -> mergeProjectChildElement(sourceChildElement, target));
    }

    private void mergeProjectChildElement(ElementWrapper sourceChildElement, ElementWrapper target) {
        String name = sourceChildElement.getNodeName();
        if ("properties".equals(name)) {
            mergeProperties(sourceChildElement, target);
        } else if (Set.of("modelVersion", "groupId", "version", "packaging", "description", "url", "scm").contains(name)) {
            mergeSingleOccurringPlainTextElementRecursively(sourceChildElement, target);
        } else if (Set.of("artifactId", "name").contains(name)) {
            // Notable elements which are not inherited include: artifactId; name; prerequisites; profiles
            // ignore
        } else if (Set.of("licenses", "developers", "distributionManagement").contains(name)) {
            mergeDeepImport(sourceChildElement, target);
        } else {
            throw new UnsupportedOperationException(String.format("Not implemented merging %s", name));
        }
    }

    private void mergeDeepImport(ElementWrapper sourceChildElement, ElementWrapper target) {
        String name = sourceChildElement.getNodeName();
        if (target.firstElement(name).isPresent()) {
            throw new UnsupportedOperationException(String.format("Cannot merge %s when it already exists", name));
        }

        ElementWrapper targetChildElement = target.ensureChild(name);
        for (var it = sourceChildElement.getChildNodesAsIterator(); it.hasNext();) {
            Node node = it.next();
            targetChildElement.appendChild(targetChildElement.importNode(node, true));
        }
    }

    private void mergeSingleOccurringPlainTextElementRecursively(ElementWrapper sourceChildElement, ElementWrapper target) {
        String name = sourceChildElement.getNodeName();
        if (sourceChildElement.hasChildElements()) {
            ElementWrapper targetChildElement = target.ensureChild(name);
            sourceChildElement.getChildElements().forEach(x -> mergeSingleOccurringPlainTextElementRecursively(x, targetChildElement));
        } else {
            sourceChildElement.getTextContentOptional().ifPresent(
                text -> {
                    target.appendIfMissing(name).ifPresent(newChildElement -> newChildElement.setTextContent(text));
                }
            );
        }
    }

    private void mergeProperties(ElementWrapper sourceProperties, ElementWrapper target) {
        sourceProperties.getChildElements()
            .forEach(p -> {
                ElementWrapper childProperties = target.ensureChild("properties");

                if (childProperties
                    .findChildElements(p.getNodeName())
                    .findAny()
                    .isPresent()) {
                    // child property already exists, so it wins
                } else {
                    childProperties.ensureChildText(p.getNodeName(), p.getTextContent());
                }
            });
    }

    private void mergeSingleOccurringPlainTextElement(ElementWrapper sourceChildElement, ElementWrapper target) {
        String name = sourceChildElement.getNodeName();
        if (target.firstElement(name).isPresent()) {
            // target wins
            return;
        }
        target.ensureChildText(name, sourceChildElement.getTextContent());
    }
}
