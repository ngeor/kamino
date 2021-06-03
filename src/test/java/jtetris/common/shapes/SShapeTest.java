package jtetris.common.shapes;

import jtetris.common.BlockType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SShape}.
 * Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class SShapeTest {
    private SShape shape;

    @Before
    public void before() {
        shape = new SShape();
    }

    @Test
    public void blockAt() throws Exception {
        char[][] expected = new char[][]{
            {' ', 'S', 'S'},
            {'S', 'S', ' '}
        };

        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                BlockType expectedBlockType = expected[row][col] == ' ' ? BlockType.Empty : BlockType.S;
                assertEquals(expectedBlockType, shape.blockAt(row, col));
            }
        }
    }

    @Test
    public void getColumns() throws Exception {
        assertEquals(3, shape.getColumns());
    }

    @Test
    public void getRows() throws Exception {
        assertEquals(2, shape.getRows());
    }
}
