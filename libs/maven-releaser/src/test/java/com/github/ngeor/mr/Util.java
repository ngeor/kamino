package com.github.ngeor.mr;

import static com.github.ngeor.mr.FnUtil.toUnaryOperator;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.AbstractThrowableAssert;

public final class Util {
    private Util() {}

    public static final String VALID_PARENT_POM_CONTENTS =
            """
        <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.acme</groupId>
            <artifactId>monorepo</artifactId>
            <version>1.0-SNAPSHOT</version>
            <packaging>pom</packaging>
            <modules>
                <module>lib</module>
            </modules>
        </project>
        """;
    public static final String VALID_CHILD_POM_CONTENTS =
            """
        <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0-SNAPSHOT</version>
            <name>foo</name>
            <description>The library</description>
            <licenses>
                <license>
                    <name>MIT</name>
                    <url>https://opensource.org/licenses/MIT</url>
                </license>
            </licenses>
            <developers>
                <developer>
                    <name>Nikolaos Georgiou</name>
                    <email>nikolaos.georgiou@gmail.com</email>
                </developer>
            </developers>
            <scm>
                <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                <tag>HEAD</tag>
                <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
            </scm>
        </project>
        """;

    public static String removeElement(String elementName) {
        return VALID_CHILD_POM_CONTENTS.replaceAll(String.format("<%s>.+?</%s>", elementName, elementName), "");
    }

    public static AbstractThrowableAssert<?, ? extends Throwable> assertInvalidPom(
            String invalidChildPomContents, Function<DocumentWrapper, ?> function) {
        return assertThatThrownBy(() -> function.apply(DocumentWrapper.parseString(invalidChildPomContents)));
    }

    public static AbstractThrowableAssert<?, ? extends Throwable> assertInvalidPom(
            String invalidChildPomContents, Consumer<DocumentWrapper> consumer) {
        return assertInvalidPom(invalidChildPomContents, toUnaryOperator(consumer));
    }

    public static AbstractThrowableAssert<?, ? extends Throwable> assertRemovingElementThrows(
            String elementName, Function<DocumentWrapper, ?> function) {
        return assertInvalidPom(removeElement(elementName), function);
    }

    public static AbstractThrowableAssert<?, ? extends Throwable> assertRemovingElementThrows(
            String elementName, Consumer<DocumentWrapper> consumer) {
        return assertRemovingElementThrows(elementName, toUnaryOperator(consumer));
    }
}
