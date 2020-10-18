package jtetris.common.graphics.swing;

import jtetris.common.graphics.Color;

/**
 * Converts colors to swing/awt Color types.
 * Created by ngeor on 16/06/17.
 */
class ColorToSwing {
    java.awt.Color convert(Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }
}
