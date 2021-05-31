package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * I shape.
 * Created by ngeor on 16/6/2017.
 */
public final class IShape extends ShapeWithoutEmptyBlocks {

    private static final int ROWS = 4;
    private static final int COLS = 1;

    public IShape() {
        super(BlockType.I, ROWS, COLS);
    }
}
