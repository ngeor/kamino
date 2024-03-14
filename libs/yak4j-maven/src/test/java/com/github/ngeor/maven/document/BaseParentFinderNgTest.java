package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

abstract class BaseParentFinderNgTest<K, V extends PomDocument> {
    @Test
    void noParent() {
        V pom = createDocument("/pom1.xml");
        BaseParentFinderNg<K, V> finder = getParentFinder();
        assertThat(finder.findParent(pom)).isEmpty();
    }

    @Test
    void noParentTwice() {
        AtomicInteger count = new AtomicInteger();
        BaseParentFinderNg<K, V> finder = getParentFinder(doc -> {
            count.incrementAndGet();
            return doc;
        });

        finder.findParent(createDocument("/pom1.xml"));
        assertThat(count.get()).isZero();
        finder.findParent(createDocument("/pom1.xml"));
        assertThat(count.get()).isZero();
    }

    @Test
    void withParent() {
        V pom = createDocument("/pom2.xml");
        BaseParentFinderNg<K, V> finder = getParentFinder();
        assertThat(finder.findParent(pom)).isNotEmpty();
    }

    @Test
    void withParentTwice() {
        AtomicInteger count = new AtomicInteger();
        BaseParentFinderNg<K, V> finder = getParentFinder(doc -> {
            count.incrementAndGet();
            return doc;
        });

        finder.findParent(createDocument("/pom2.xml"));
        assertThat(count.get()).isEqualTo(1);
        finder.findParent(createDocument("/pom2.xml"));
        assertThat(count.get()).isEqualTo(1);
    }

    protected abstract BaseParentFinderNg<K, V> getParentFinder(UnaryOperator<V> decorator);

    protected BaseParentFinderNg<K, V> getParentFinder() {
        return getParentFinder(UnaryOperator.identity());
    }

    protected abstract V createDocument(String resourceName);

}
