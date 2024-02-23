package jtetris.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import org.junit.Test;

/**
 * Unit test for {@link BlockType}.
 * Created by ngeor on 15/6/2017.
 */
public class BlockTypeTest {
    @Test
    public void isEmpty_onEmptyBlock_isTrue() {
        assertTrue(BlockType.Empty.isEmpty());
    }

    @Test
    public void isEmpty_onSquareBlock_isFalse() {
        assertFalse(BlockType.O.isEmpty());
    }

    @Test
    public void random_eventuallyReturnsAllBlockTypesExceptEmpty() {
        // arrange
        BlockType[] values = BlockType.values();
        EnumSet<BlockType> found = EnumSet.noneOf(BlockType.class);

        // act
        while (found.size() < values.length - 1) {
            found.add(BlockType.random());
        }

        // assert
        assertFalse(found.contains(BlockType.Empty));
    }
}
