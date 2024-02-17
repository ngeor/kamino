package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * Empty shape.
 * Created by ngeor on 16/6/2017.
 */
public final class EmptyShape extends ShapeBase {
    @Override
    public int getColumns() {
        return 0;
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    protected BlockType safeBlockAt(int row, int col) {
        throw new IndexOutOfBoundsException();
    }
}
