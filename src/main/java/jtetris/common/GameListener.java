package jtetris.common;

/**
 * Game listener consumes events fired by the game.
 */
public interface GameListener {
    void deletingRow(int row);

    void stateChanged();

    void moved(MovingShape old, MovingShape current);

    void shapeChanged(MovingShape nextBlock);
}
