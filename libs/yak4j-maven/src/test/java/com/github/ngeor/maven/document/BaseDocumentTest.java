package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Nested
    class Coordinates {
        @ParameterizedTest
        @ValueSource(strings = {ElementNames.GROUP_ID, ElementNames.ARTIFACT_ID, ElementNames.VERSION})
        void missingElementNoParent(String elementName) {
            BaseDocument doc = new ResourceDocument(factory, "/pom1.xml", d -> {
                d.getDocumentElement().removeChildNodesByName(elementName);
                return d;
            });
            assertThatThrownBy(doc::coordinates)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot resolve coordinates, " + elementName + " is missing");
        }

        @ParameterizedTest
        @ValueSource(strings = {ElementNames.GROUP_ID, ElementNames.ARTIFACT_ID, ElementNames.VERSION})
        void emptyElementNoParent(String elementName) {
            BaseDocument doc = new ResourceDocument(factory, "/pom1.xml", d -> {
                d.getDocumentElement().firstElement(elementName).ifPresent(e -> e.setTextContent(""));
                return d;
            });
            assertThatThrownBy(doc::coordinates)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot resolve coordinates, " + elementName + " is missing");
        }

        @Test
        void groupIdIsInherited() {
            BaseDocument doc = new ResourceDocument(factory, "/pom2.xml", d -> {
                d.getDocumentElement().firstElement(ElementNames.GROUP_ID).ifPresent(e -> e.setTextContent(""));
                return d;
            });
            assertThat(doc.coordinates()).isEqualTo(new MavenCoordinates("com.shared", "ball", "1.0.1"));
        }

        @Test
        void artifactIdIsNotInherited() {
            BaseDocument doc = new ResourceDocument(factory, "/pom2.xml", d -> {
                d.getDocumentElement().firstElement(ElementNames.ARTIFACT_ID).ifPresent(e -> e.setTextContent(""));
                return d;
            });
            assertThatThrownBy(doc::coordinates)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot resolve coordinates, " + ElementNames.ARTIFACT_ID + " is missing");
        }

        @Test
        void versionIsInherited() {
            BaseDocument doc = new ResourceDocument(factory, "/pom2.xml", d -> {
                d.getDocumentElement().firstElement(ElementNames.VERSION).ifPresent(e -> e.setTextContent(""));
                return d;
            });
            assertThat(doc.coordinates()).isEqualTo(new MavenCoordinates("com.future", "ball", "2.0"));
        }

        @ParameterizedTest
        @ValueSource(strings = {ElementNames.GROUP_ID, ElementNames.ARTIFACT_ID, ElementNames.VERSION})
        void missingParentElement(String elementName) {
            BaseDocument doc = new ResourceDocument(factory, "/pom2.xml", d -> {
                d.getDocumentElement().removeChildNodesByName(ElementNames.VERSION);
                d.getDocumentElement()
                        .firstElement(ElementNames.PARENT)
                        .ifPresent(e -> e.removeChildNodesByName(elementName));
                return d;
            });
            assertThatThrownBy(doc::coordinates).hasMessage(elementName + " is missing from parent coordinates");
        }
    }
}
