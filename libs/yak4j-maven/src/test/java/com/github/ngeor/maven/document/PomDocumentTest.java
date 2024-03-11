package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.Objects;
import java.util.Optional;
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
        Repository repository = new ResourceRepository();
        PomDocument parent = pomDocument.parent(
            repository
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

    @Test
    void effectivePomMergesParentWithGrandparentOnlyOnce() {
        PomDocument child1 = loadPomDocument("/2level/child1.xml");
        PomDocument child2 = loadPomDocument("/2level/child2.xml");
        Repository repository = new ResourceRepository();
        MergerNg merger = (left, right) -> left;
        EffectivePomDocument effective1 = child1.effectivePom(repository, merger);
        EffectivePomDocument effective2 = child2.effectivePom(repository, merger);
    }

    private PomDocument loadPomDocument(String resourceName) {
        return new ResourcePomDocument(resourceName);
    }

    private DocumentWrapper loadDocumentWrapper(String resourceName) {
        Objects.requireNonNull(resourceName);
        Validate.isTrue(resourceName.startsWith("/"));
        return DocumentWrapper.parse(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
    }

    private static class ResourceRepository implements Repository {

        @Override
        public Optional<PomDocument> findParent(PomDocument pomDocument) {
            if (pomDocument instanceof ResourcePomDocument r) {
                String parentResourceName = switch (r.getResourceName()) {
                    case "/pom2.xml" -> "/pom3.xml";
                    case "/2level/child1.xml", "/2level/child2.xml" -> "/2level/parent.xml";
                    case "/2level/parent.xml" -> "/2level/grandparent.xml";
                    default -> null;
                };
                return Optional.ofNullable(parentResourceName).map(ResourcePomDocument::new);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
