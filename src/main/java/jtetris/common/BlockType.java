package jtetris.common;

import java.util.Random;

/**
 * Defines the possible block types.
 */
public enum BlockType {
    /**
     * EmptyShape block.
     */
    Empty,

    /**
     * Long "I" block.
     */
    I,

    /**
     * "J" block.
     */
    J,

    /**
     * "L" block.
     */
    L,

    /**
     * Square block.
     */
    O,

    /**
     * "S" block.
     */
    S,

    /**
     * "T" block.
     */
    T,

    /**
     * "Z" block.
     */
    Z;

    /**
     * Gets a random block.
     */
    public static BlockType random() {
        BlockType[] blockTypes = BlockType.values();
        BlockType result = BlockType.Empty;
        Random rnd = new Random();
        while (result == BlockType.Empty) {
            result = blockTypes[rnd.nextInt(blockTypes.length)];
        }

        return result;
    }

    public boolean isEmpty() {
        return this == BlockType.Empty;
    }

    public boolean isNonEmpty() {
        return !isEmpty();
    }
}
