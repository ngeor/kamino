package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.tuple.Pair;

public final class Finders {
    private Finders() {}

    public static Finder<ElementWrapper, Pair<String, String>> groupIdArtifactId() {
        return new CompositeTextFinder(
            new TextFinder(ElementNames.GROUP_ID),
            new TextFinder(ElementNames.ARTIFACT_ID)
        );
    }

    public static Finder<ElementWrapper, MavenCoordinates> mavenCoordinates() {
        return new MappingFinder<>(new CompositeFinder<>(
            groupIdArtifactId(),
            new TextFinder(ElementNames.VERSION)
        ), x -> new MavenCoordinates(x.getLeft().getLeft(), x.getLeft().getRight(), x.getRight().orElse(null)));
    }

    public static Finder<ElementWrapper, ParentPom> parentPom() {
        return new MappingFinder<>(new CompositeFinder<>(
            mavenCoordinates(),
            new TextFinder(ElementNames.RELATIVE_PATH)
        ), x-> new ParentPom(x.getLeft(), x.getRight().orElse(null)));
    }
}
