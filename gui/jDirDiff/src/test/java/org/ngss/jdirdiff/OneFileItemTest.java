package org.ngss.jdirdiff;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;

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
