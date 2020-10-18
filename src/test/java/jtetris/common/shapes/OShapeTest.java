package jtetris.common.shapes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jtetris.common.BlockType;

import static org.junit.Assert.*;

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
    public void blockAt() throws Exception {
        for (int row = 0; row < shape.getRows(); row++) {
            for (int col = 0; col < shape.getColumns(); col++) {
                Assert.assertEquals(BlockType.O, shape.blockAt(row, col));
            }
        }
    }

    @Test
    public void getColumns() throws Exception {
        assertEquals(2, shape.getColumns());
    }

    @Test
    public void getRows() throws Exception {
        assertEquals(2, shape.getRows());
    }
}
