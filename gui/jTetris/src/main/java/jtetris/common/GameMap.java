package jtetris.common;

/**
 * Game map holds all placed shapes.
 */
public class GameMap implements BlockComposite {
    private static final int COLS = 10;
    private static final int ROWS = 20;
    private final BlockType[][] map = new BlockType[ROWS][COLS];

    GameMap() {
        reset();
    }

    /**
     * Absorbs the given shape into the map.
     */
    void absorb(MovingShape currentObject) {
        for (int row = 0; row < currentObject.getShapeDefinition().getRows(); row++) {
            for (int col = 0; col < currentObject.getShapeDefinition().getColumns(); col++) {
                BlockType blockType = currentObject.getShapeDefinition().blockAt(row, col);

                if (blockType.isNonEmpty()) {
                    map[row + currentObject.getRow()][col + currentObject.getColumn()] = blockType;
                }
            }
        }
    }

    @Override
    public BlockType blockAt(int row, int col) {
        return map[row][col];
    }

    /**
     * Checks if the given object can move into the given coordinates.
     */
    boolean canMoveTo(BlockComposite currentObject, int newRow, int newCol) {
        boolean result;
        if (newCol < 0) {
            result = false;
        } else if (newRow + currentObject.getRows() > ROWS) {
            result = false;
        } else if (newCol + currentObject.getColumns() > COLS) {
            result = false;
        } else {
            for (int row = 0; row < currentObject.getRows(); row++) {
                for (int col = 0; col < currentObject.getColumns(); col++) {
                    if (currentObject.blockAt(row, col).isNonEmpty() && isMapNonEmpty(row + newRow, col + newCol)) {
                        return false;
                    }
                }
            }

            result = true;
        }

        return result;
    }

    @Override
    public int getColumns() {
        return COLS;
    }

    @Override
    public int getRows() {
        return ROWS;
    }

    /**
     * Deletes a row if it is completely filled with blocks.
     */
    boolean purgeRow(int row) {
        if (isRowFull(row)) {
            deleteRow(row);
            return true;
        }

        return false;
    }

    /**
     * Resets the map to its initial empty state.
     */
    void reset() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                map[row][col] = BlockType.Empty;
            }
        }
    }

    private void deleteRow(int rowToDelete) {
        for (int row = rowToDelete; row >= 0; row--) {
            for (int col = 0; col < COLS; col++) {
                map[row][col] = row > 0 ? map[row - 1][col] : BlockType.Empty;
            }
        }
    }

    private boolean isMapNonEmpty(int row, int col) {
        return row >= 0 && map[row][col].isNonEmpty();
    }

    private boolean isRowFull(int row) {
        for (BlockType cell : map[row]) {
            if (cell.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
