package com.github.ngeor.yak4jdom;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class TypedMavenDocumentTest {
    @Test
    void test()
            throws InvocationTargetException, InstantiationException, IllegalAccessException,
                    ClassNotFoundException {
        String pom =
                """
        <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.acme</groupId>
            <artifactId>dummy</artifactId>
            <version>1.0-SNAPSHOT</version>
            <properties>
                <foo>123</foo>
                <bar>test</bar>
            </properties>
            <modules>
                <module>module1</module>
                <module>module2</module>
            </modules>
            <licenses>
                <license>
                    <name>MIT</name>
                    <url>whatever</url>
                </license>
            </licenses>
        </project>
        """;
        DocumentWrapper document = DocumentWrapper.parseString(pom);
        TypedMavenDocument typedMavenDocument = document.asTyped(TypedMavenDocument.class);
        assertThat(typedMavenDocument.modelVersion()).isEqualTo("4.0.0");
        assertThat(typedMavenDocument.groupId()).isEqualTo("com.acme");
        assertThat(typedMavenDocument.artifactId()).isEqualTo("dummy");
        assertThat(typedMavenDocument.description()).isNull();
        assertThat(typedMavenDocument.modules()).containsExactly("module1", "module2");
        assertThat(typedMavenDocument.licenses()).containsExactly(new TypedMavenDocument.License("MIT", "whatever"));
    }
}
