package jtetris.common.shapes;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link EmptyShape}.
 * Created by ngeor on 16/6/2017.
 */
public class EmptyShapeTest {
    private EmptyShape shape;

    @Before
    public void before() {
        shape = new EmptyShape();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void blockAt() {
        shape.blockAt(0, 0);
    }

    @Test
    public void getColumns() {
        assertEquals(0, shape.getColumns());
    }

    @Test
    public void getRows() {
        assertEquals(0, shape.getRows());
    }
}
