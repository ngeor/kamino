package com.github.ngeor.maven.find;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.GroupIdArtifactId;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.function.Function;

public final class States {
    private States() {}

    public static State<ElementWrapper, ElementWrapper> element(String elementName) {
        return new ElementState<>(elementName, Function.identity());
    }

    public static State<ElementWrapper, String> text(String elementName) {
        return new ElementState<>(elementName, e -> e.getTextContentTrimmed().orElse(null));
    }

    public static State<ElementWrapper, GroupIdArtifactId> groupIdArtifactId() {
        return text(ElementNames.GROUP_ID).compose(text(ElementNames.ARTIFACT_ID), GroupIdArtifactId::new);
    }

    public static State<ElementWrapper, MavenCoordinates> mavenCoordinates() {
        return groupIdArtifactId().compose(text(ElementNames.VERSION), MavenCoordinates::new);
    }

    public static State<ElementWrapper, ParentPom> parentPom() {
        return mavenCoordinates().compose(text(ElementNames.RELATIVE_PATH), ParentPom::new);
    }
}
