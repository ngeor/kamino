package jtetris.swing;

import java.awt.*;
import javax.swing.*;
import jtetris.common.MovingShape;

/**
 * Represents a shape.
 */
class ShapeComponent extends JComponent {
    private MovingShape shape;

    public MovingShape getShape() {
        return shape;
    }

    /**
     * Sets the current shape.
     */
    public void setShape(MovingShape shape) {
        this.shape = new MovingShape(shape.getShapeDefinition(), 0, 0);
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        Dimension size = getSize();
        g.setColor(Color.white);
        g.fillRect(0, 0, size.width, size.height);

        if (shape == null) {
            return;
        }

        final int maxCols = 4;
        final int maxRows = 4;
        int cellSize = Math.min(size.width / maxCols, size.height / maxRows);
        BlockPainter blockPainter = BlockPainter.instance();
        blockPainter.paint(shape, g, cellSize);
    }
}
