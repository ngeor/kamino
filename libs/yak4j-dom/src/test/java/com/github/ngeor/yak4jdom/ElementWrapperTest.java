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
}
