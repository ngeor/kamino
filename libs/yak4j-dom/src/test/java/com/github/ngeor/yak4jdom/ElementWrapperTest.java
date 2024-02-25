package com.github.ngeor.yak4jdom;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ElementWrapperTest {
    @Nested
    class Path {
        @Test
        void test() {
            String contents =
                    """
                <project>
                    <parent>
                        <groupId />
                    </parent>
                </project>""";
            DocumentWrapper document = DocumentWrapper.parseString(contents);
            ElementWrapper rootElement = document.getDocumentElement();
            assertThat(rootElement.path()).isEqualTo("project");
            ElementWrapper firstChild = rootElement.firstElement("parent").orElseThrow();
            assertThat(firstChild.path()).isEqualTo("project/parent");
            ElementWrapper firstGrandChild = firstChild.firstElement("groupId").orElseThrow();
            assertThat(firstGrandChild.path()).isEqualTo("project/parent/groupId");
        }
    }

    @Nested
    class FirstElementsText {
        @Test
        void test() {
            String contents =
                    """
                <project>
                    <version>1.0</version>
                    <groupId>acme</groupId>
                    <irrelevant>whatever</irrelevant>
                </project>""";
            DocumentWrapper document = DocumentWrapper.parseString(contents);
            ElementWrapper element = document.getDocumentElement();
            String[] result = element.firstElementsText("groupId", "artifactId", "version");
            assertThat(result).containsExactly("acme", null, "1.0");
        }

        @Test
        void testFirstElementWins() {
            String contents =
                    """
                <project>
                    <version>2.0</version>
                    <version>2.1</version>
                    <groupId>acme</groupId>
                    <artifactId>foo</artifactId>
                </project>""";
            DocumentWrapper document = DocumentWrapper.parseString(contents);
            ElementWrapper element = document.getDocumentElement();
            String[] result = element.firstElementsText("groupId", "artifactId", "version");
            assertThat(result).containsExactly("acme", "foo", "2.0");
        }
    }
}
