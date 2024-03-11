package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Test;

class PomDocumentTest {
    @Test
    void test1() {
        PomDocument pomDocument = load("/pom1.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(
            new MavenCoordinates("com.acme", "foo", "1.0")
        );
        assertThat(pomDocument.parentPom()).isEmpty();
    }

    @Test
    void test2() {
        PomDocument pomDocument = load("/pom2.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(
            new MavenCoordinates("com.future", "ball", "1.0.1")
        );
        assertThat(pomDocument.parentPom()).contains(new ParentPom("com.shared", "bar", "2.0", null));
    }

    private PomDocument load(String resourceName) {
        Objects.requireNonNull(resourceName);
        Validate.isTrue(resourceName.startsWith("/"));
        return new PomDocument(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
    }
}
