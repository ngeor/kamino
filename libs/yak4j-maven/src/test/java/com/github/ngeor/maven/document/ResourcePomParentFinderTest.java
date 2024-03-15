package com.github.ngeor.maven.document;

import java.util.function.UnaryOperator;

class ResourcePomParentFinderTest extends BaseParentFinderNgTest<String, ResourcePomDocument> {

    @Override
    protected BaseParentFinderNg<String, ResourcePomDocument> getParentFinder(
            UnaryOperator<ResourcePomDocument> decorator) {
        return new ResourcePomParentFinder(decorator);
    }

    @Override
    protected ResourcePomDocument createDocument(String resourceName) {
        return new ResourcePomDocument(resourceName);
    }
}
