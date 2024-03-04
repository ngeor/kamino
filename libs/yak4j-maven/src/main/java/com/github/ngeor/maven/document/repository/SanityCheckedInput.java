package com.github.ngeor.maven.document.repository;

import com.github.ngeor.maven.document.loader.DocumentLoader;
import com.github.ngeor.maven.document.loader.DocumentLoaderDecorator;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

final class SanityCheckedInput extends DocumentLoaderDecorator {
    private SanityCheckedInput(DocumentLoader decorated) {
        super(decorated);
    }

    @Override
    public DocumentWrapper loadDocument() {
        DocumentWrapper result = super.loadDocument();
        Objects.requireNonNull(result);
        ElementWrapper element = Objects.requireNonNull(result.getDocumentElement());
        Validate.isTrue(
                ElementNames.PROJECT.equals(element.getNodeName()),
                "Unexpected root element '%s' (expected '%s')",
                element.getNodeName(),
                ElementNames.PROJECT);
        return result;
    }

    public static DocumentLoaderFactory<DocumentLoader> decorateFactory(DocumentLoaderFactory<DocumentLoader> factory) {
        return pomFile -> new SanityCheckedInput(factory.createDocumentLoader(pomFile));
    }
}
