package jtetris.swing;

import java.awt.*;
import jtetris.common.BlockComposite;
import jtetris.common.BlockType;
import jtetris.common.GameMap;
import jtetris.common.MovingShape;

/**
 * Handles rendering of blocks.
 */
final class BlockPainter {
    private static final BlockPainter INSTANCE = new BlockPainter();
    private final ColorMapper colorMapper = new ColorMapper();

    private BlockPainter() {}

    public static BlockPainter instance() {
        return INSTANCE;
    }

    /**
     * Erases the shape.
     */
    void erase(BlockComposite blockComposite, Graphics g, int cellSize, int relativeRow, int relativeCol) {
        for (int row = 0; row < blockComposite.getRows(); row++) {
            int absoluteRow = row + relativeRow;
            for (int col = 0; col < blockComposite.getColumns(); col++) {
                int absoluteCol = col + relativeCol;
                eraseCell(blockComposite.blockAt(row, col), g, cellSize, absoluteRow, absoluteCol);
            }
        }
    }

    /**
     * Renders the shape.
     */
    void paint(BlockComposite blockComposite, Graphics g, int cellSize, int relativeRow, int relativeCol) {
        for (int row = 0; row < blockComposite.getRows(); row++) {
            int absoluteRow = row + relativeRow;
            for (int col = 0; col < blockComposite.getColumns(); col++) {
                int absoluteCol = col + relativeCol;
                drawCell(blockComposite.blockAt(row, col), g, cellSize, absoluteRow, absoluteCol);
            }
        }
    }

    void paint(GameMap map, Graphics g, int cellSize) {
        paint(map, g, cellSize, 0, 0);
    }

    void paint(MovingShape shape, Graphics g, int cellSize) {
        paint(shape.getShapeDefinition(), g, cellSize, shape.getRow(), shape.getColumn());
    }

    private void drawCell(BlockType blockType, Graphics g, int cellSize, int row, int col) {
        if (blockType.isNonEmpty()) {
            int left = col * cellSize;
            int top = row * cellSize;
            Color baseColor = colorMapper.getColor(blockType);
            g.setColor(baseColor);
            g.fillRect(left, top, cellSize, cellSize);

            Color darkColor = baseColor.darker();
            Color brightColor = baseColor.brighter();
            g.setColor(brightColor);
            g.drawLine(left, top, left, top + cellSize - 1);
            g.drawLine(left, top, left + cellSize - 1, top);
            g.setColor(darkColor);
            g.drawLine(left, top + cellSize - 1, left + cellSize - 1, top + cellSize - 1);
            g.drawLine(left + cellSize - 1, top, left + cellSize - 1, top + cellSize - 1);
        }
    }

    private void eraseCell(BlockType blockType, Graphics g, int cellSize, int row, int col) {
        if (blockType.isNonEmpty()) {
            int left = col * cellSize;
            int top = row * cellSize;
            Color baseColor = Color.white;
            g.setColor(baseColor);
            g.fillRect(left, top, cellSize, cellSize);
        }
    }
}
