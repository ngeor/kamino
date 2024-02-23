package jtetris.common.shapes;

import static org.junit.Assert.assertEquals;

import jtetris.common.BlockType;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link TShape}.
 * Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class TShapeTest {
    private TShape shape;

    @Before
    public void before() {
        shape = new TShape();
    }

    @Test
    public void blockAt() {
        char[][] expected = new char[][] {
            {'T', 'T', 'T'},
            {' ', 'T', ' '}
        };

        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                BlockType expectedBlockType = expected[row][col] == ' ' ? BlockType.Empty : BlockType.T;
                assertEquals(expectedBlockType, shape.blockAt(row, col));
            }
        }
    }

    @Test
    public void getColumns() {
        assertEquals(3, shape.getColumns());
    }

    @Test
    public void getRows() {
        assertEquals(2, shape.getRows());
    }
}
