package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.Validate;

class ResourcePomDocument extends PomDocument {
    private final String resourceName;
    private final UnaryOperator<DocumentWrapper> documentDecorator;

    public ResourcePomDocument(String resourceName, UnaryOperator<DocumentWrapper> documentDecorator) {
        this.resourceName = Objects.requireNonNull(resourceName);
        this.documentDecorator = Objects.requireNonNull(documentDecorator);
        Validate.isTrue(resourceName.startsWith("/"));
    }

    public ResourcePomDocument(String resourceName) {
        this(resourceName, UnaryOperator.identity());
    }

    @Override
    protected DocumentWrapper doLoadDocument() {
        return documentDecorator.apply(DocumentWrapper.parse(Objects.requireNonNull(getClass().getResourceAsStream(resourceName))));
    }

    public String getResourceName() {
        return resourceName;
    }
}
