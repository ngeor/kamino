package com.github.ngeor.maven.document;

import java.util.Optional;

class ResourcePomParentFinder implements ParentFinderNg {

    @Override
    public Optional<PomDocument> findParent(PomDocument pomDocument) {
        if (pomDocument instanceof ResourcePomDocument r) {
            String parentResourceName =
                    switch (r.getResourceName()) {
                        case "/pom2.xml" -> "/pom3.xml";
                        case "/2level/child1.xml", "/2level/child2.xml" -> "/2level/parent.xml";
                        case "/2level/parent.xml" -> "/2level/grandparent.xml";
                        default -> null;
                    };
            return Optional.ofNullable(parentResourceName).map(ResourcePomDocument::new);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
