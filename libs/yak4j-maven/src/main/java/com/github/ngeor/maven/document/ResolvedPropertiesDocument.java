package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Map;
import java.util.stream.Collectors;

public class ResolvedPropertiesDocument extends BaseDocument {
    protected ResolvedPropertiesDocument(BaseDocument decorated) {
        super(decorated.getOwner(), () -> resolveProperties(decorated.loadDocument()));
    }

    private static DocumentWrapper resolveProperties(DocumentWrapper document) {
        Map<String, String> unresolvedProperties = document.getDocumentElement()
                .findChildElements("properties")
                .flatMap(ElementWrapper::getChildElements)
                .collect(Collectors.toMap(ElementWrapper::getNodeName, ElementWrapper::getTextContent));
        if (unresolvedProperties.isEmpty()) {
            return document;
        }

        // resolve them
        Map<String, String> resolvedProperties = StringPropertyResolver.resolve(unresolvedProperties);
        DocumentWrapper result = document.deepClone();
        boolean hadChanges = result.getDocumentElement()
                .transformTextNodes(text -> StringPropertyResolver.resolve(text, resolvedProperties::get));
        return hadChanges ? result : document;
    }

    @Override
    public ResolvedPropertiesDocument toResolvedProperties() {
        return this;
    }
}
