package jtetris.common.graphics.swing;

import static org.junit.Assert.assertEquals;

import jtetris.common.graphics.Color;
import org.junit.Test;

/**
 * Unit test for {@link ColorToSwing}.
 * Created by ngeor on 16/06/17.
 */
@SuppressWarnings("MagicNumber")
public class ColorToSwingTest {
    @Test
    public void test() {
        final int red = 10;
        final int green = 20;
        final int blue = 30;
        Color color = new Color(red, green, blue);
        java.awt.Color awtColor = new ColorToSwing().convert(color);
        assertEquals("red", red, awtColor.getRed());
        assertEquals("green", green, awtColor.getGreen());
        assertEquals("blue", blue, awtColor.getBlue());
    }
}
