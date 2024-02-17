package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * S shape.
 * Created by ngeor on 16/6/2017.
 */
public final class SShape extends ShapeWithEmptyBlocks {
    /**
     * Creates an instance of this class.
     */
    public SShape() {
        super(BlockType.S, new char[][] {
            {' ', 'S', 'S'},
            {'S', 'S', ' '}
        });
    }
}
