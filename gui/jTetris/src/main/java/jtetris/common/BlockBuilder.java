package jtetris.common;

import jtetris.common.shapes.*;

/**
 * Creates standard shapes out of the known block types.
 */
class BlockBuilder {
    /**
     * Creates a new shape for the given block type.
     */
    Shape create(BlockType blockType) {
        Shape result;
        switch (blockType) {
            case Empty:
                result = new EmptyShape();
                break;
            case I:
                result = new IShape();
                break;
            case J:
                result = new JShape();
                break;
            case L:
                result = new LShape();
                break;
            case O:
                result = new OShape();
                break;
            case S:
                result = new SShape();
                break;
            case T:
                result = new TShape();
                break;
            case Z:
                result = new ZShape();
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return result;
    }
}
