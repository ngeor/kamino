package jtetris.common;

import java.util.EnumSet;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link BlockType}.
 * Created by ngeor on 15/6/2017.
 */
public class BlockTypeTest {
    @Test
    public void isEmpty_onEmptyBlock_isTrue() throws Exception {
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
