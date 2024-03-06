package com.github.ngeor.mr;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class MavenReleaserTest {
    private final Function<DocumentWrapper, MavenCoordinates> function = MavenReleaser::calcModuleCoordinates;

    @Test
    void testGroupIdIsRequired() {
        Util.assertRemovingElementThrows(ElementNames.GROUP_ID, function)
                .hasMessage("Cannot resolve coordinates, parent element is missing");
    }

    @Test
    void testArtifactIdIsRequired() {
        Util.assertRemovingElementThrows(ElementNames.ARTIFACT_ID, function).hasMessageContaining("artifactId");
    }

    @Test
    void testVersionIsRequired() {
        Util.assertRemovingElementThrows(ElementNames.VERSION, function)
                .hasMessage("Cannot resolve coordinates, parent element is missing");
    }
}
