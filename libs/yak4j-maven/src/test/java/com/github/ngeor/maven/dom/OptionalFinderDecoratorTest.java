package com.github.ngeor.maven.dom;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.Iterator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class OptionalFinderDecoratorTest {
    private final Finder<ElementWrapper, Pair<String, String>> finder = Finders.text(
        ElementNames.GROUP_ID
    ).compose(groupIdFinder -> Finders.text(ElementNames.VERSION).asOptional(groupIdFinder::hasResult));

    @Test
    void testSkipAtFirstMatch() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId>com.acme</groupId>
                <version>1.0</version>
                <artifactId>foo</artifactId>
            </project>
            """);
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);

        // act
        Pair<String, String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull();
        assertThat(value.getLeft()).isEqualTo("com.acme");
        assertThat(value.getRight()).isNull();
        assertThat(cit.getHasNextCount()).isEqualTo(1);
        assertThat(cit.getNextCount()).isEqualTo(1);
    }

    @Test
    void testSkipAtFirstMatchReversed() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <version>1.0</version>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
            </project>
            """);
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);

        // act
        Pair<String, String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull();
        assertThat(value.getLeft()).isEqualTo("com.acme");
        assertThat(value.getRight()).isEqualTo("1.0");
        assertThat(cit.getHasNextCount()).isEqualTo(2);
        assertThat(cit.getNextCount()).isEqualTo(2);
    }

    @Test
    void testKeepSearchingIfFirstMatchIsEmpty() {
        // arrange
        DocumentWrapper doc = DocumentWrapper.parseString("""
            <project>
                <groupId />
                <version>1.0</version>
                <artifactId>foo</artifactId>
            </project>
            """);
        ElementWrapper documentElement = doc.getDocumentElement();
        Iterator<ElementWrapper> it = documentElement.getChildElementsAsIterator();
        CountingIterator<ElementWrapper> cit = new CountingIterator<>(it);

        // act
        Pair<String, String> value = finder.find(cit);

        // assert
        assertThat(value).isNotNull();
        assertThat(value.getLeft()).isNull();
        assertThat(value.getRight()).isEqualTo("1.0");
        assertThat(cit.getHasNextCount()).isEqualTo(2);
        assertThat(cit.getNextCount()).isEqualTo(2);
    }
}
