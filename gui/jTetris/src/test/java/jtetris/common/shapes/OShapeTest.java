package jtetris.common.shapes;

import static org.junit.Assert.assertEquals;

import jtetris.common.BlockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link OShape}.
 * Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class OShapeTest {
    private OShape shape;

    @Before
    public void before() {
        shape = new OShape();
    }

    @Test
    public void blockAt() {
        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                Assert.assertEquals(BlockType.O, shape.blockAt(row, col));
            }
        }
    }

    @Test
    public void getColumns() {
        assertEquals(2, shape.getColumns());
    }

    @Test
    public void getRows() {
        assertEquals(2, shape.getRows());
    }
}
