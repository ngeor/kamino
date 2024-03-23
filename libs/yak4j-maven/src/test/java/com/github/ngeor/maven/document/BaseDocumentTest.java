package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
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

    @Nested
    class Modules {
        @Test
        void empty() {
            BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
            assertThat(doc.modules()).isEmpty();
        }

        @Test
        void notEmpty() {
            BaseDocument doc = new ResourceDocument(factory, "/aggregator1/pom1.xml");
            assertThat(doc.modules()).containsExactly("child1", "child2", "child3");
        }
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
                    .isInstanceOf(NullPointerException.class)
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
                    .isInstanceOf(NullPointerException.class)
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
                    .isInstanceOf(NullPointerException.class)
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

    @Nested
    class GetProperty {
        @Test
        void missingProperty() {
            BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
            assertThat(doc.getProperty("color")).isEmpty();
        }

        @Test
        void presentProperty() {
            BaseDocument doc = new ResourceDocument(factory, "/2level/grandparent.xml");
            assertThat(doc.getProperty("color")).contains("blue");
        }
    }

    @Nested
    class Dependencies {
        @Test
        void dependency() {
            BaseDocument doc = new ResourceDocument(factory, "/2level/parent.xml");
            assertThat(doc.dependencies()).containsExactly(new MavenCoordinates("com.external", "library", "1.2.3"));
        }
    }

    @Nested
    class ParentPomTest {
        @Test
        void missing() {
            BaseDocument doc = new ResourceDocument(factory, "/pom1.xml");
            assertThat(doc.parentPom()).isEmpty();
        }

        @Test
        void present() {
            BaseDocument doc = new ResourceDocument(factory, "/pom2.xml");
            assertThat(doc.parentPom()).contains(new ParentPom("com.shared", "bar", "2.0", null));
        }

        @Test
        void withRelativePath() {
            BaseDocument doc = new ResourceDocument(factory, "/aggregator2/child3.xml");
            assertThat(doc.parentPom()).contains(new ParentPom("com.acme", "child2", "2.2", "../child2"));
        }
    }
}
