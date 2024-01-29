package jtetris.common;

/**
 * Represents a group of blocks.
 */
public interface BlockComposite {
    BlockType blockAt(int row, int col);

    int getColumns();

    int getRows();
}
