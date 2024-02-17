package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * Z shape.
 * Created by ngeor on 16/6/2017.
 */
public final class ZShape extends ShapeWithEmptyBlocks {
    /**
     * Creates an instance of this class.
     */
    public ZShape() {
        super(BlockType.Z, new char[][] {
            {'Z', 'Z', ' '},
            {' ', 'Z', 'Z'}
        });
    }
}
