package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.function.UnaryOperator;

class ResourceDocumentTest extends BasePomDocumentTest<ResourcePomDocument> {
    @Override
    protected ResourcePomDocument createDocument(
            String resourceName, UnaryOperator<DocumentWrapper> documentDecorator) {
        return new ResourcePomDocument(resourceName, documentDecorator);
    }
}
