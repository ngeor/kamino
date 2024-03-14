package com.github.ngeor.maven.document;

import com.github.ngeor.maven.dom.ParentPom;
import java.util.function.UnaryOperator;

class ResourcePomParentFinder extends BaseParentFinderNg<String, ResourcePomDocument> {

    ResourcePomParentFinder(UnaryOperator<ResourcePomDocument> decorator) {
        super(decorator);
    }

    ResourcePomParentFinder() {
        this(UnaryOperator.identity());
    }

    @Override
    protected String cacheKey(ResourcePomDocument child) {
        return child.getResourceName();
    }

    @Override
    protected ResourcePomDocument doFindParent(ResourcePomDocument child, ParentPom parentPom) {
        String parentResourceName =
                switch (child.getResourceName()) {
                    case "/pom2.xml" -> "/pom3.xml";
                    case "/2level/child1.xml", "/2level/child2.xml" -> "/2level/parent.xml";
                    case "/2level/parent.xml" -> "/2level/grandparent.xml";
                    default -> null;
                };
        return new ResourcePomDocument(parentResourceName);
    }
}
