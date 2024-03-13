package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PomDocumentTest {
    @Test
    void test1() {
        PomDocument pomDocument = loadPomDocument("/pom1.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
        assertThat(pomDocument.parentPom()).isEmpty();
    }

    @Test
    void test2() {
        PomDocument pomDocument = loadPomDocument("/pom2.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(new MavenCoordinates("com.future", "ball", "1.0.1"));
        assertThat(pomDocument.parentPom()).contains(new ParentPom("com.shared", "bar", "2.0", null));
        Repository repository = new ResourceRepository();
        PomDocument parent = pomDocument.parent(repository).orElseThrow();
        assertThat(parent).isNotNull();
        assertThat(parent.coordinates()).isEqualTo(new MavenCoordinates("com.shared", "bar", "2.0"));
    }

    @Test
    void test3() {
        PomDocument pomDocument = loadPomDocument("/pom3.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(new MavenCoordinates("com.shared", "bar", "2.0"));
        assertThat(pomDocument.parentPom()).isEmpty();
    }

    @Test
    void effectivePomMergesParentWithGrandparentOnlyOnce() {
        PomDocument child1 = loadPomDocument("/2level/child1.xml");
        PomDocument child2 = loadPomDocument("/2level/child2.xml");
        Repository repository = new ResourceRepository();
        MergerNg defaultMerger = new DefaultMergerNg();
        CountingMerger countingMerger = new CountingMerger(defaultMerger);
        MergerNg merger = new CacheMergerNg(countingMerger);
        EffectivePomDocument effective1 = child1.effectivePom(repository, merger);
        EffectivePomDocument effective2 = child2.effectivePom(repository, merger);
        assertThat(countingMerger.counts)
                .containsOnly(
                        Map.entry(
                                new MavenCoordinates("com.acme", "grandparent", "4.0"),
                                Map.of(new MavenCoordinates("com.acme", "parent", "5.0"), 1)),
                        Map.entry(
                                new MavenCoordinates("com.acme", "parent", "5.0"),
                                Map.of(
                                        new MavenCoordinates("com.acme", "child1", "6.0"), 1,
                                        new MavenCoordinates("com.acme", "child2", "7.0"), 1)));
    }

    private PomDocument loadPomDocument(String resourceName) {
        return new ResourcePomDocument(resourceName);
    }

    private static class ResourceRepository implements Repository {

        @Override
        public Optional<PomDocument> findParent(PomDocument pomDocument) {
            if (pomDocument instanceof ResourcePomDocument r) {
                String parentResourceName =
                        switch (r.getResourceName()) {
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

    private static class CountingMerger implements MergerNg {
        private final Map<MavenCoordinates, Map<MavenCoordinates, Integer>> counts = new HashMap<>();
        private final MergerNg decorated;

        private CountingMerger(MergerNg decorated) {
            this.decorated = Objects.requireNonNull(decorated);
        }

        @Override
        public EffectivePomDocument merge(PomDocument root) {
            return decorated.merge(root);
        }

        @Override
        public EffectivePomDocument merge(EffectivePomDocument left, PomDocument right) {
            counts.computeIfAbsent(left.coordinates(), ignored -> new HashMap<>())
                    .compute(right.coordinates(), (k, v) -> v == null ? 1 : v + 1);
            return decorated.merge(left, right);
        }
    }
}
