package jtetris.common.shapes;

import jtetris.common.BlockType;

/**
 * J shape.
 * Created by ngeor on 16/6/2017.
 */
public final class JShape extends ShapeWithEmptyBlocks {
    /**
     * Creates an instance of this class.
     */
    public JShape() {
        super(BlockType.J, new char[][] {
            {' ', 'J'},
            {' ', 'J'},
            {'J', 'J'}
        });
    }
}
