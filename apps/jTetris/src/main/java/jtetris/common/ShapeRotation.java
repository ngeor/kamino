package jtetris.common;

/**
 * Rotates a shape.
 * Created by ngeor on 16/6/2017.
 */
public class ShapeRotation implements Shape {
    private final Shape original;

    ShapeRotation(Shape original) {
        this.original = original;
    }

    @Override
    public BlockType blockAt(int row, int col) {
        return original.blockAt(getColumns() - col - 1, row);
    }

    @Override
    public int getColumns() {
        return original.getRows();
    }

    @Override
    public int getRows() {
        return original.getColumns();
    }
}
