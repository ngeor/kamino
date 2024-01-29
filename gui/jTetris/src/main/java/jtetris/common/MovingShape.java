package jtetris.common;

/**
 * A moving shape.
 * Created by ngeor on 16/6/2017.
 */
public class MovingShape {
    private final Shape shapeDefinition;
    private final int row;
    private final int column;

    /**
     * Creates an instances of this class.
     */
    public MovingShape(Shape shapeDefinition, int row, int column) {
        this.shapeDefinition = shapeDefinition;
        this.row = row;
        this.column = column;
    }

    public Shape getShapeDefinition() {
        return shapeDefinition;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    MovingShape moveLeft() {
        return new MovingShape(shapeDefinition, row, column - 1);
    }

    MovingShape moveRight() {
        return new MovingShape(shapeDefinition, row, column + 1);
    }

    MovingShape moveDown() {
        return new MovingShape(shapeDefinition, row + 1, column);
    }

    MovingShape rotate() {
        return new MovingShape(new ShapeRotation(shapeDefinition), row, column);
    }

    public int getColumns() {
        return shapeDefinition.getColumns();
    }

    public int getRows() {
        return shapeDefinition.getRows();
    }
}
