package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.tuple.Pair;

public final class Finders {
    private Finders() {}


    public static Finder<ElementWrapper, String> text(String elementName) {
        return new TextFinder(elementName);
    }

    public static Finder<ElementWrapper, Pair<String, String>> groupIdArtifactId() {
        return text(ElementNames.GROUP_ID).compose(text(ElementNames.ARTIFACT_ID));
    }

    public static Finder<ElementWrapper, MavenCoordinates> mavenCoordinates() {
        return groupIdArtifactId().compose(text(ElementNames.VERSION))
            .map(x -> new MavenCoordinates(x.getLeft().getLeft(), x.getLeft().getRight(), x.getRight()));
    }

    public static Finder<ElementWrapper, ParentPom> parentPom() {
        return mavenCoordinates().compose(text(ElementNames.RELATIVE_PATH))
            .map(x-> new ParentPom(x.getLeft(), x.getRight()));
    }
}
