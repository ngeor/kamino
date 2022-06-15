package org.ngss.jdirdiff;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link OneFileItem}.
 */
public class OneFileItemTest {
    @Test
    public void test() {
        File file = new File("/tmp");
        OneFileItem i = new OneFileItem(file);
        assertEquals(file, i.getFile());
    }
}
