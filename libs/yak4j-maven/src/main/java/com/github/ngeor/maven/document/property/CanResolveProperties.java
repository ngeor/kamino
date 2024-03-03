package com.github.ngeor.maven.document.property;

import com.github.ngeor.maven.document.effective.EffectivePom;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Map;

public interface CanResolveProperties extends EffectivePom {
    default DocumentWrapper resolveProperties() {
        DocumentWrapper document = effectivePom();
        Map<String, String> unresolvedProperties = DomHelper.getProperties(document);
        if (unresolvedProperties == null || unresolvedProperties.isEmpty()) {
            return document;
        }

        // resolve them
        Map<String, String> resolvedProperties = PropertyResolver.resolve(unresolvedProperties);
        DocumentWrapper result = document.deepClone();
        boolean hadChanges = result.getDocumentElement()
                .transformTextNodes(text -> PropertyResolver.resolve(text, resolvedProperties::get));
        return hadChanges ? result : document;
    }
}
