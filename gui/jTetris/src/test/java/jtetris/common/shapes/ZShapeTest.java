package jtetris.common.shapes;

import static org.junit.Assert.assertEquals;

import jtetris.common.BlockType;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ZShape}. Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class ZShapeTest {
    private ZShape shape;

    @Before
    public void before() {
        shape = new ZShape();
    }

    @Test
    public void blockAt() throws Exception {
        char[][] expected = new char[][] {{'Z', 'Z', ' '}, {' ', 'Z', 'Z'}};

        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                BlockType expectedBlockType = expected[row][col] == ' ' ? BlockType.Empty : BlockType.Z;
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
