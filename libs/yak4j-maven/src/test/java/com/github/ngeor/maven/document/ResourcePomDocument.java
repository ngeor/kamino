package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

class ResourcePomDocument extends PomDocument {
    private final String resourceName;

    public ResourcePomDocument(String resourceName) {
        this.resourceName = Objects.requireNonNull(resourceName);
        Validate.isTrue(resourceName.startsWith("/"));
    }

    @Override
    protected DocumentWrapper doLoadDocument() {
        return DocumentWrapper.parse(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
    }

    public String getResourceName() {
        return resourceName;
    }
}
