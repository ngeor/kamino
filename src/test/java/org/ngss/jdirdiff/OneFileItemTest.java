package org.ngss.jdirdiff;

import java.io.File;
import org.junit.Test;

import static org.junit.Assert.*;

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
