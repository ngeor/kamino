package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * A shape that does not have empty blocks (that's shapes: I and O).
 */
public abstract class ShapeWithoutEmptyBlocks extends ShapeBase {
    private final BlockType blockType;
    private final int rows;
    private final int cols;

    /**
     * Creates an instance of this class.
     */
    ShapeWithoutEmptyBlocks(BlockType blockType, int rows, int cols) {
        this.blockType = blockType;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public int getColumns() {
        return cols;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    protected BlockType safeBlockAt(int row, int col) {
        return blockType;
    }
}
