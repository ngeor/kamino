package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * A shape that contains some empty blocks.
 */
public abstract class ShapeWithEmptyBlocks extends ShapeBase {
    private final char[][] data;
    private final BlockType blockType;

    ShapeWithEmptyBlocks(BlockType blockType, char[][] data) {
        this.blockType = blockType;
        this.data = data;
    }

    @Override
    public int getColumns() {
        return data[0].length;
    }

    @Override
    public int getRows() {
        return data.length;
    }

    @Override
    protected BlockType safeBlockAt(int row, int col) {
        return data[row][col] == ' ' ? BlockType.Empty : blockType;
    }
}
