package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.dom.ParentPom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class PomDocumentTest {
    @Test
    void test2() {
        PomDocument pomDocument = loadPomDocument("/pom2.xml");
        MavenCoordinates coordinates = pomDocument.coordinates();
        assertThat(coordinates).isEqualTo(new MavenCoordinates("com.future", "ball", "1.0.1"));
        assertThat(pomDocument.parentPom()).contains(new ParentPom("com.shared", "bar", "2.0", null));
        ParentFinderNg parentFinder = new ResourcePomParentFinder();
        PomDocument parent = pomDocument.parent(parentFinder).orElseThrow();
        assertThat(parent).isNotNull();
        assertThat(parent.coordinates()).isEqualTo(new MavenCoordinates("com.shared", "bar", "2.0"));
    }

    @Test
    void effectivePomMergesParentWithGrandparentOnlyOnce() {
        PomDocument child1 = loadPomDocument("/2level/child1.xml");
        PomDocument child2 = loadPomDocument("/2level/child2.xml");
        ParentFinderNg parentFinder = new ResourcePomParentFinder();
        MergerNg defaultMerger = new DefaultMergerNg();
        CountingMerger countingMerger = new CountingMerger(defaultMerger);
        MergerNg merger = new CacheMergerNg(countingMerger);
        EffectivePomDocument effective1 = child1.effectivePom(parentFinder, merger);
        EffectivePomDocument effective2 = child2.effectivePom(parentFinder, merger);
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
