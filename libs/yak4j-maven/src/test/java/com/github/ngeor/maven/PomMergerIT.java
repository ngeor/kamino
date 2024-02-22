package com.github.ngeor.maven;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PomMergerIT {
    @TempDir
    private Path tempDirectory;

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectory(tempDirectory.resolve("lib"));
    }

    @Test
    void testMergeProperties() throws IOException {
        String parent =
                """
        <project>
            <properties>
                <bar>abc</bar>
                <name>test</name>
            </properties>
        </project>
        """;
        String child =
                """
        <project>
            <properties>
                <foo>123</foo>
                <bar>hello</bar>
            </properties>
        </project>
        """;
        String expected =
                """
        <project>
            <properties>
                <bar>hello</bar>
                <name>test</name>
                <foo>123</foo>
            </properties>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(expected);
    }

    @Test
    void testMergePropertiesChildHasNoProperties() throws IOException {
        String parent =
                """
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
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(parent);
    }

    @Test
    void testMergePropertiesParentHasNoProperties() throws IOException {
        String child =
                """
    <project>
        <properties>
            <foo>bar</foo>
        </properties>
    </project>
    """;
        String parent = """
        <project>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(child);
    }

    @Test
    void testMergeGroupId() throws IOException {
        String parent = """
        <project>
            <groupId>com.acme</groupId>
        </project>
        """;
        String child = """
        <project>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(parent);
    }

    @Test
    void testMergeGroupIdOverride() throws IOException {
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
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(child);
    }

    @Test
    void testDoNotInheritName() throws IOException {
        String parent = """
        <project>
            <name>com.acme</name>
        </project>
        """;
        String child = """
        <project/>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(child);
    }

    @Test
    void testMergeLicenses() throws IOException {
        String parent =
                """
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
        assertThat(actual).isEqualToNormalizingNewlines(parent);
    }

    @Test
    void testMergeScm() throws IOException {
        String parent =
                """
        <project>
            <scm>
                <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                <tag>HEAD</tag>
                <url>https://github.com/ngeor/kamino/tree/master</url>
            </scm>
        </project>
        """;
        String child =
                """
        <project>
            <scm>
                <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
            </scm>
        </project>
        """;
        String expected =
                """
        <project>
            <scm>
                <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                <tag>HEAD</tag>
                <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
            </scm>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(expected);
    }

    @Test
    void testMergeBuildPlugins() throws IOException {
        String parent =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                        <scope>test</scope>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;

        String child =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <scope>compile</scope>
                    </plugin>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>bar</artifactId>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;

        String expected =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                        <scope>compile</scope>
                    </plugin>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>bar</artifactId>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(expected);
    }

    @Test
    void testMergeBuildPluginExecutions() throws IOException {
        String parent =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                        <executions>
                            <execution>
                                <id>pre-test</id>
                                <goals>
                                    <goal>test</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>test</id>
                                <goals>
                                    <goal>verify</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;

        String child =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <executions>
                            <execution>
                                <id>pre-test</id>
                                <goals>
                                    <goal>validate</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>post-test</id>
                                <goals>
                                    <goal>report</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;

        String expected =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                        <executions>
                            <execution>
                                <id>pre-test</id>
                                <goals>
                                    <goal>validate</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>test</id>
                                <goals>
                                    <goal>verify</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>post-test</id>
                                <goals>
                                    <goal>report</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(expected);
    }

    @Test
    void testMergeBuildPluginConfiguration() throws IOException {
        String parent =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                        <configuration>
                            <foo>1</foo>
                        </configuration>
                        <executions>
                            <execution>
                                <id>pre-test</id>
                                <goals>
                                    <goal>test</goal>
                                </goals>
                                <configuration>
                                    <foo>2</foo>
                                </configuration>
                            </execution>
                            <execution>
                                <id>test</id>
                                <goals>
                                    <goal>verify</goal>
                                </goals>
                                <configuration>
                                    <foo>3</foo>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;

        String child =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <configuration>
                            <foo>4</foo>
                        </configuration>
                        <executions>
                            <execution>
                                <id>pre-test</id>
                                <goals>
                                    <goal>validate</goal>
                                </goals>
                                <configuration>
                                    <foo>5</foo>
                                </configuration>
                            </execution>
                            <execution>
                                <id>post-test</id>
                                <goals>
                                    <goal>report</goal>
                                </goals>
                                <configuration>
                                    <foo>6</foo>
                                    <rules>
                                        <rule>6.a</rule>
                                        <rule>6.b</rule>
                                    </rules>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;

        String expected =
                """
        <project>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.acme</groupId>
                        <artifactId>foo</artifactId>
                        <version>1.0</version>
                        <configuration>
                            <foo>1</foo>
                            <foo>4</foo>
                        </configuration>
                        <executions>
                            <execution>
                                <id>pre-test</id>
                                <goals>
                                    <goal>validate</goal>
                                </goals>
                                <configuration>
                                    <foo>2</foo>
                                    <foo>5</foo>
                                </configuration>
                            </execution>
                            <execution>
                                <id>test</id>
                                <goals>
                                    <goal>verify</goal>
                                </goals>
                                <configuration>
                                    <foo>3</foo>
                                </configuration>
                            </execution>
                            <execution>
                                <id>post-test</id>
                                <goals>
                                    <goal>report</goal>
                                </goals>
                                <configuration>
                                    <foo>6</foo>
                                    <rules>
                                        <rule>6.a</rule>
                                        <rule>6.b</rule>
                                    </rules>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </project>
        """;
        String actual = merge(parent, child);
        assertThat(actual).isEqualToNormalizingNewlines(expected);
    }

    private String merge(String parent, String child) throws IOException {
        Files.writeString(tempDirectory.resolve("pom.xml"), parent);
        Files.writeString(
                tempDirectory.resolve("lib").resolve("pom.xml"),
                child.replace(
                        "<project>",
                        """
            <project>
                <parent>
                    <relativePath>..</relativePath>
                    <artifactId>whatever</artifactId>
                </parent>
            """));

        MavenDocument childDoc = new MavenDocument(
                tempDirectory.resolve("lib").resolve("pom.xml").toFile());
        MavenDocument effectiveChild = childDoc.effectivePom();
        DocumentWrapper result = effectiveChild.getDocument();
        result.indent();
        return result.writeToString();
    }
}
