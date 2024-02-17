package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * T shape.
 * Created by ngeor on 16/6/2017.
 */
public final class TShape extends ShapeWithEmptyBlocks {
    /**
     * Creates an instance of this class.
     */
    public TShape() {
        super(BlockType.T, new char[][] {
            {'T', 'T', 'T'},
            {' ', 'T', ' '}
        });
    }
}
