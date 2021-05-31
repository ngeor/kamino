package jtetris.common.shapes;

import org.junit.Before;
import org.junit.Test;

import jtetris.common.BlockType;

import static org.junit.Assert.*;

/**
 * Unit test for {@link JShape}.
 * Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class JShapeTest {
    private JShape shape;

    @Before
    public void before() {
        shape = new JShape();
    }

    @Test
    public void blockAt() throws Exception {
        char[][] expected = new char[][] {
                {' ', 'J'},
                {' ', 'J'},
                {'J', 'J'}
        };

        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                BlockType expectedBlockType = expected[row][col] == ' ' ? BlockType.Empty : BlockType.J;
                assertEquals(expectedBlockType, shape.blockAt(row, col));
            }
        }
    }

    @Test
    public void getColumns() throws Exception {
        assertEquals(2, shape.getColumns());
    }

    @Test
    public void getRows() throws Exception {
        assertEquals(3, shape.getRows());
    }
}
