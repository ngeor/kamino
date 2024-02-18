package com.github.ngeor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PomMergerTest {
    @Test
    void testMergeProperties() {
        String parent = """
        <project>
            <properties>
                <bar>abc</bar>
                <name>test</name>
            </properties>
        </project>
        """;
        String child = """
        <project>
            <properties>
                <foo>123</foo>
                <bar>hello</bar>
            </properties>
        </project>
        """;
        String expected = """
        <project>
            <properties>
                <foo>123</foo>
                <bar>hello</bar>
                <name>test</name>
            </properties>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMergePropertiesChildHasNoProperties() {
        String parent = """
        <project>
            <properties>
                <foo>bar</foo>
            </properties>
        </project>
        """;
        String child = """
        <project>
        </project>
        """;
        String expected = """
        <project>
            <properties>
                <foo>bar</foo>
            </properties>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMergeGroupId() {
        String parent = """
        <project>
            <groupId>com.acme</groupId>
        </project>
        """;
        String child = """
        <project>
        </project>
        """;
        String expected = """
        <project>
            <groupId>com.acme</groupId>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMergeGroupIdOverride() {
        String parent = """
        <project>
            <groupId>com.acme</groupId>
        </project>
        """;
        String child = """
        <project>
            <groupId>com.foo</groupId>
        </project>
        """;
        String expected = """
        <project>
            <groupId>com.foo</groupId>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testDoNotMergeName() {
        String parent = """
        <project>
            <name>com.acme</name>
        </project>
        """;
        String child = """
        <project>
        </project>
        """;
        String expected = """
        <project>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testMergeLicenses() {
        String parent = """
        <project>
            <licenses>
                <license>
                    <name>MIT</name>
                    <url>https://opensource.org/licenses/MIT</url>
                </license>
            </licenses>
        </project>
        """;
        String child = """
        <project>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(parent);
    }

    @Test
    void testMergeScm() {
        String parent = """
        <project>
            <scm>
                <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                <tag>HEAD</tag>
                <url>https://github.com/ngeor/kamino/tree/master</url>
            </scm>
        </project>
        """;
        String child = """
        <project>
            <scm>
                <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
            </scm>
        </project>
        """;
        String expected = """
        <project>
            <scm>
                <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                <tag>HEAD</tag>
            </scm>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualTo(expected);
    }

    private static String merge(String parent, String child) {
        return new PomMerger().merge(parent, child).replace(System.lineSeparator(), "\n");
    }
}