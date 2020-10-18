package jtetris.common.graphics.swing;

import jtetris.common.graphics.Color;
import jtetris.common.graphics.Graphics;

/**
 * Swing/AWT implementation for Graphics.
 * Created by ngeor on 16/06/17.
 */
public class GraphicsSwingImpl implements Graphics {
    private java.awt.Graphics graphics;
    private ColorToSwing colorToSwing;

    @Override
    public void setFill(Color color) {
        graphics.setColor(colorToSwing.convert(color));
    }
}
