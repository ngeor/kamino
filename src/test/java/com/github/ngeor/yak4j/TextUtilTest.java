package com.github.ngeor.yak4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TextUtil}.
 */
class TextUtilTest {
    @Test
    void filter() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/sample-pom.xml");
        TextUtil textUtil = new TextUtil();
        List<String[]> list = textUtil.filter(inputStream, Pattern.compile("<version>(.+?)</version>"));
        assertThat(list).containsExactly(
            new String[] {"  <version>0.4.3</version>", "0.4.3"}
        );
    }
}
