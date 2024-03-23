package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

class FindersTest {
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
        var finder = Finders.text(ElementNames.GROUP_ID);

        // act
        String value = finder.find(cit);

        // assert
        assertThat(value).isEqualTo("com.acme");
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
        var finder = Finders.text(ElementNames.GROUP_ID);

        // act
        String value = finder.find(cit);

        // assert
        assertThat(value).isEqualTo("com.acme2");
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
        var finder = Finders.text(ElementNames.GROUP_ID);

        // act
        String value = finder.find(cit);

        // assert
        assertThat(value).isEqualTo("com.acme1");
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
        var finder = Finders.text(ElementNames.GROUP_ID);

        // act
        String value = finder.find(cit);

        // assert
        assertThat(value).isEqualTo("com.acme1");
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
        var finder = Finders.text(ElementNames.GROUP_ID);

        // act
        String value = finder.find(cit);

        // assert
        assertThat(value).isNull();
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
        var finder = Finders.text(ElementNames.GROUP_ID);

        // act
        String value = finder.find(cit);

        // assert
        assertThat(value).isNull();
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
        var compositeFinder = Finders.text(ElementNames.GROUP_ID).compose(
            Finders.text(ElementNames.VERSION)
        );

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
