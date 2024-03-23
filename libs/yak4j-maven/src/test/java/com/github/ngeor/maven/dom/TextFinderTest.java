package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TextFinderTest {
    @Test
    void testGroupIdIsFirstElement() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId>com.acme</groupId>
                <version>1.0</version>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder finder = new TextFinder(ElementNames.GROUP_ID);

        // act
        Optional<String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull().contains("com.acme");
        assertThat(cit.getHasNextCount()).isEqualTo(1);
        assertThat(cit.getNextCount()).isEqualTo(1);
    }

    @Test
    void testGroupIdIsLastElement() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <version>1.0</version>
                <groupId>com.acme2</groupId>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder finder = new TextFinder(ElementNames.GROUP_ID);

        // act
        Optional<String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull().contains("com.acme2");
        assertThat(cit.getHasNextCount()).isEqualTo(2);
        assertThat(cit.getNextCount()).isEqualTo(2);
    }

    @Test
    void testFirstGroupIdWins() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId>com.acme1</groupId>
                <groupId>com.acme2</groupId>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder finder = new TextFinder(ElementNames.GROUP_ID);

        // act
        Optional<String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull().contains("com.acme1");
        assertThat(cit.getHasNextCount()).isEqualTo(1);
        assertThat(cit.getNextCount()).isEqualTo(1);
    }

    @Test
    void testTrim() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId>
                    com.acme1
                </groupId>
                <groupId>com.acme2</groupId>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder finder = new TextFinder(ElementNames.GROUP_ID);

        // act
        Optional<String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull().contains("com.acme1");
        assertThat(cit.getHasNextCount()).isEqualTo(1);
        assertThat(cit.getNextCount()).isEqualTo(1);
    }

    @Test
    void testEmptyWins() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId> </groupId>
                <groupId>com.acme2</groupId>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder finder = new TextFinder(ElementNames.GROUP_ID);

        // act
        Optional<String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull().isEmpty();
        assertThat(cit.getHasNextCount()).isEqualTo(1);
        assertThat(cit.getNextCount()).isEqualTo(1);
    }

    @Test
    void testNotPresent() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <version>1.0</version>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder finder = new TextFinder(ElementNames.GROUP_ID);

        // act
        Optional<String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull().isEmpty();
        assertThat(cit.getHasNextCount()).isEqualTo(2);
        assertThat(cit.getNextCount()).isEqualTo(1);
    }

    @Test
    void testCompositeFinder() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId>com.acme</groupId>
                <version>1.0</version>
            </project>""");
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);
        TextFinder groupIdFinder = new TextFinder(ElementNames.GROUP_ID);
        TextFinder versionFinder = new TextFinder(ElementNames.VERSION);
        CompositeTextFinder compositeFinder = new CompositeTextFinder(groupIdFinder, versionFinder);

        // act
        Pair<String, String> value = compositeFinder.find(cit);

        // assert
        assertThat(value).isNotNull();
        assertThat(value.getLeft()).isEqualTo("com.acme");
        assertThat(value.getRight()).isEqualTo("1.0");
        assertThat(cit.getHasNextCount()).isEqualTo(2);
        assertThat(cit.getNextCount()).isEqualTo(2);
    }
}
