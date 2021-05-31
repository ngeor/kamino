package jtetris.common;

import org.junit.Test;

import jtetris.common.shapes.IShape;
import jtetris.common.shapes.JShape;

import static org.junit.Assert.*;

/**
 * Unit test for {@link ShapeRotation}.
 * Created by ngeor on 16/6/2017.
 */
@SuppressWarnings("MagicNumber")
public class ShapeRotationTest {
    @Test
    public void rotateI() {
        ShapeRotation rotated = new ShapeRotation(new IShape());
        assertEquals(4, rotated.getColumns());
        assertEquals(1, rotated.getRows());
        for (int row = 0; row < rotated.getRows(); row++) {
            for (int col = 0; col < rotated.getColumns(); col++) {
                assertEquals(BlockType.I, rotated.blockAt(row, col));
            }
        }
    }

    @Test
    public void rotateJ() {
        ShapeRotation rotated = new ShapeRotation(new JShape());
        assertEquals(3, rotated.getColumns());
        assertEquals(2, rotated.getRows());

        char[][] expectedData = new char[][]{
                {'J', ' ', ' '},
                {'J', 'J', 'J'}
        };

        for (int row = 0; row < rotated.getRows(); row++) {
            for (int col = 0; col < rotated.getColumns(); col++) {
                BlockType expected = expectedData[row][col] == ' ' ? BlockType.Empty : BlockType.J;
                assertEquals(expected, rotated.blockAt(row, col));
            }
        }
    }

    @Test
    public void rotateJTwice() {
        ShapeRotation rotated = new ShapeRotation(new ShapeRotation(new JShape()));
        assertEquals(2, rotated.getColumns());
        assertEquals(3, rotated.getRows());

        char[][] expectedData = new char[][]{
                {'J', 'J'},
                {'J', ' '},
                {'J', ' '}
        };

        for (int row = 0; row < rotated.getRows(); row++) {
            for (int col = 0; col < rotated.getColumns(); col++) {
                BlockType expected = expectedData[row][col] == ' ' ? BlockType.Empty : BlockType.J;
                assertEquals(expected, rotated.blockAt(row, col));
            }
        }
    }
}
