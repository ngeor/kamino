package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Test;

class PomDocumentTest {
    @Test
    void test1() {
        PomDocument pomDocument = loadPomDocument("/pom1.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(
            new MavenCoordinates("com.acme", "foo", "1.0")
        );
        assertThat(pomDocument.parentPom()).isEmpty();
    }

    @Test
    void test2() {
        PomDocument pomDocument = loadPomDocument("/pom2.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(
            new MavenCoordinates("com.future", "ball", "1.0.1")
        );
        assertThat(pomDocument.parentPom()).contains(new ParentPom("com.shared", "bar", "2.0", null));
        PomDocument parent = pomDocument.parent(
            ignored -> loadDocumentWrapper("/pom3.xml")
        ).orElseThrow();
        assertThat(parent).isNotNull();
        assertThat(parent.coordinates()).isEqualTo(new MavenCoordinates("com.shared", "bar", "2.0"));
    }

    @Test
    void test3() {
        PomDocument pomDocument = loadPomDocument("/pom3.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(
            new MavenCoordinates("com.shared", "bar", "2.0")
        );
        assertThat(pomDocument.parentPom()).isEmpty();
    }

    private PomDocument loadPomDocument(String resourceName) {
        return new PomDocument(loadDocumentWrapper(resourceName));
    }

    private DocumentWrapper loadDocumentWrapper(String resourceName) {
        Objects.requireNonNull(resourceName);
        Validate.isTrue(resourceName.startsWith("/"));
        return DocumentWrapper.parse(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
    }
}
