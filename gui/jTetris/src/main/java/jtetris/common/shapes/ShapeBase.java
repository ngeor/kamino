package jtetris.common.shapes;

import jtetris.common.BlockType;
import jtetris.common.Shape;

/**
 * Base class for shapes.
 * Created by ngeor on 16/6/2017.
 */
public abstract class ShapeBase implements Shape {
    @Override
    public BlockType blockAt(int row, int col) {
        if (row < 0 || row >= getRows()) {
            throw new IndexOutOfBoundsException("row");
        }

        if (col < 0 || col >= getColumns()) {
            throw new IndexOutOfBoundsException("col");
        }

        return safeBlockAt(row, col);
    }

    protected abstract BlockType safeBlockAt(int row, int col);
}
