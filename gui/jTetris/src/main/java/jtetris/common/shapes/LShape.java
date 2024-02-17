package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * L shape.
 * Created by ngeor on 16/6/2017.
 */
public final class LShape extends ShapeWithEmptyBlocks {
    /**
     * Creates an instance of this class.
     */
    public LShape() {
        super(BlockType.L, new char[][] {
            {'L', ' '},
            {'L', ' '},
            {'L', 'L'}
        });
    }
}
