package jtetris.common.shapes;

import static org.junit.Assert.assertEquals;

import jtetris.common.BlockType;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link IShape}.
 * Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class IShapeTest {
    private IShape shape;

    @Before
    public void before() {
        shape = new IShape();
    }

    @Test
    public void blockAt() {
        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                assertEquals(BlockType.I, shape.blockAt(row, col));
            }
        }
    }

    @Test
    public void getColumns() {
        assertEquals(1, shape.getColumns());
    }

    @Test
    public void getRows() {
        assertEquals(4, shape.getRows());
    }
}
