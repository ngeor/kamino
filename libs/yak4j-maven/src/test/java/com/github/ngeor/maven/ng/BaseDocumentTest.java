package com.github.ngeor.maven.ng;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Test;

class BaseDocumentTest {
    private final PomDocumentFactory factory = new PomDocumentFactory();

    @Test
    void getOwner() {
        BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
        assertThat(doc.getOwner()).isSameAs(factory);
    }

    @Test
    void loadDocument() {
        BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
        DocumentWrapper document1 = doc.loadDocument();
        assertThat(document1).isNotNull();
        DocumentWrapper document2 = doc.loadDocument();
        assertThat(document2).isSameAs(document1);
    }

    @Test
    void coordinates() {
        BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
        MavenCoordinates coordinates = doc.coordinates();
        assertThat(coordinates).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
    }

    @Test
    void parentPom() {
        BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
        assertThat(doc.parentPom()).isEmpty();
    }

    @Test
    void modules() {
        BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
        assertThat(doc.modules()).isEmpty();
    }
}
