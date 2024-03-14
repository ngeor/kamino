package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BasePomDocumentTest {
    @Test
    void coordinatesTwiceOnlyLoadsDocumentOnce() {
        AtomicInteger count = new AtomicInteger();
        ResourcePomDocument pom = new ResourcePomDocument("/pom1.xml", doc -> {
            count.incrementAndGet();
            return doc;
        });
        assertThat(count.get()).isZero();
        MavenCoordinates coordinates = pom.coordinates();
        assertThat(coordinates).isNotNull();
        assertThat(count.get()).isEqualTo(1);
        assertThat(pom.coordinates()).isEqualTo(coordinates);
        assertThat(count.get()).isEqualTo(1);
    }

    @Nested
    class CoordinatesWithoutParent {
        private final String resourceName = "/pom1.xml";

        @Test
        void allCoordinatesPresent() {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName);
            MavenCoordinates coordinates = pom.coordinates();
            assertThat(coordinates).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            ElementNames.GROUP_ID,
            ElementNames.ARTIFACT_ID,
            ElementNames.VERSION
        })
        void oneCoordinateMissing(String elementName) {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName, doc -> {
                doc.getDocumentElement().removeChildNodesByName(elementName);
                return doc;
            });
            assertThatThrownBy(pom::coordinates).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot resolve coordinates, " + elementName + " is missing");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            ElementNames.GROUP_ID,
            ElementNames.ARTIFACT_ID,
            ElementNames.VERSION
        })
        void oneCoordinateEmpty(String elementName) {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName, doc -> {
                doc.getDocumentElement().firstElement(elementName).ifPresent(e -> e.setTextContent(""));
                return doc;
            });
            assertThatThrownBy(pom::coordinates).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot resolve coordinates, " + elementName + " is missing");
        }
    }

    @Nested
    class CoordinatesWithParent {
        private final String resourceName = "/pom2.xml";

        @Test
        void allCoordinatesPresent() {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName);
            MavenCoordinates coordinates = pom.coordinates();
            assertThat(coordinates).isEqualTo(new MavenCoordinates("com.future", "ball", "1.0.1"));
        }

        @Test
        void groupIdIsInherited() {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName, doc -> {
                doc.getDocumentElement().removeChildNodesByName(ElementNames.GROUP_ID);
                return doc;
            });
            MavenCoordinates coordinates = pom.coordinates();
            assertThat(coordinates).isEqualTo(new MavenCoordinates("com.shared", "ball", "1.0.1"));
        }

        @Test
        void artifactIdIsNotInherited() {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName, doc -> {
                doc.getDocumentElement().removeChildNodesByName(ElementNames.ARTIFACT_ID);
                return doc;
            });
            assertThatThrownBy(pom::coordinates).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot resolve coordinates, " + ElementNames.ARTIFACT_ID + " is missing");
        }

        @Test
        void versionIsInherited() {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName, doc -> {
                doc.getDocumentElement().removeChildNodesByName(ElementNames.VERSION);
                return doc;
            });
            MavenCoordinates coordinates = pom.coordinates();
            assertThat(coordinates).isEqualTo(new MavenCoordinates("com.future", "ball", "2.0"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            ElementNames.GROUP_ID,
            ElementNames.VERSION
        })
        void parentCoordinatesAreMandatory(String elementName) {
            ResourcePomDocument pom = new ResourcePomDocument(resourceName, doc -> {
                doc.getDocumentElement().removeChildNodesByName(elementName);
                doc.getDocumentElement().firstElement(ElementNames.PARENT).ifPresent(e -> e.removeChildNodesByName(elementName));
                return doc;
            });
            assertThatThrownBy(pom::coordinates)
                .hasMessage(elementName + " is missing from parent coordinates");
        }
    }

    @Nested
    class ParentPomTest {
        @Test
        void withoutParent() {
            ResourcePomDocument pom = new ResourcePomDocument("/pom1.xml");
            assertThat(pom.parentPom()).isEmpty();
        }

        @Test
        void withParent() {
            ResourcePomDocument pom = new ResourcePomDocument("/pom2.xml");
            assertThat(pom.parentPom()).contains(new ParentPom("com.shared", "bar", "2.0", null));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            ElementNames.GROUP_ID,
            ElementNames.ARTIFACT_ID,
            ElementNames.VERSION
        })
        void parentCoordinatesAreMandatory(String elementName) {
            ResourcePomDocument pom = new ResourcePomDocument("/pom2.xml", doc -> {
                doc.getDocumentElement().firstElement(ElementNames.PARENT).ifPresent(e -> e.removeChildNodesByName(elementName));
                return doc;
            });
            assertThatThrownBy(pom::parentPom)
                .hasMessage(elementName + " is missing from parent coordinates");
        }
    }
}
