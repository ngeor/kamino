package jtetris.common;

import java.util.ArrayList;

/**
 * Represents a game.
 */
public class Game {
    private static final int LINES_PER_LEVEL = 5;
    private final GameMap gameMap = new GameMap();
    private final ArrayList<GameListener> listeners = new ArrayList<>();
    private MovingShape current;
    private int level;
    private int linesCleared;
    private MovingShape next;
    private final BlockBuilder blockBuilder = new BlockBuilder();
    private GameState state = GameState.NotStarted;

    //
    // Getters
    //

    public synchronized GameState getState() {
        return state;
    }

    public synchronized MovingShape getCurrent() {
        return current;
    }

    public synchronized int getLevel() {
        return level;
    }

    public synchronized int getLinesCleared() {
        return linesCleared;
    }

    public synchronized GameMap getMap() {
        return gameMap;
    }

    public synchronized MovingShape getNext() {
        return next;
    }

    //
    // Events
    //

    /**
     * Adds a game listener.
     */
    public synchronized void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeGameListener(GameListener listener) {
        listeners.remove(listener);
    }

    //
    // Handling user input
    //

    /**
     * Causes the current shape to drop all the way to the bottom.
     */
    public synchronized void drop() {
        if (state == GameState.Started) {
            MovingShape nextShape;
            do {
                nextShape = current.moveDown();
            } while (setCurrent(nextShape));

            finishMoveDown();
        }
    }

    /**
     * Moves the shape down by one line.
     */
    public synchronized void moveDown() {
        if (state == GameState.Started) {
            if (!setCurrent(current.moveDown())) {
                finishMoveDown();
            }
        }
    }

    /**
     * Moves the shape to the left.
     */
    public synchronized void moveLeft() {
        if (state == GameState.Started) {
            setCurrent(current.moveLeft());
        }
    }

    /**
     * Moves the shape to the right.
     */
    public synchronized void moveRight() {
        if (state == GameState.Started) {
            setCurrent(current.moveRight());
        }
    }

    /**
     * Rotates the shape.
     */
    public synchronized void rotate() {
        if (state == GameState.Started) {
            setCurrent(current.rotate());
        }
    }

    /**
     * Resets the game.
     */
    public synchronized void reset() {
        linesCleared = 0;
        level = 0;
        selectNextBlock();
        gameMap.reset();
        state = GameState.NotStarted;
    }

    /**
     * Starts the game.
     */
    public synchronized void start() {
        if (state == GameState.NotStarted || state == GameState.Paused) {
            setState(GameState.Started);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Pauses the game.
     */
    public synchronized void pause() {
        if (state == GameState.Started) {
            setState(GameState.Paused);
        } else {
            throw new IllegalStateException();
        }
    }

    //
    // Firing events
    //

    private void fireDeletingRow(int row) {
        for (GameListener listener : listeners) {
            listener.deletingRow(row);
        }
    }

    private void fireStateChanged() {
        for (GameListener listener : listeners) {
            listener.stateChanged();
        }
    }

    private void fireMoved(MovingShape old) {
        for (GameListener listener : listeners) {
            listener.moved(old, current);
        }
    }

    private void fireShapeChanged() {
        for (GameListener listener : listeners) {
            listener.shapeChanged(next);
        }
    }

    //
    // Setters
    //

    private void setState(GameState state) {
        this.state = state;
        fireStateChanged();
    }

    private boolean setCurrent(MovingShape newObject) {
        ensureGameNotOver();
        if (canChangeCurrent(newObject)) {
            changeCurrentShape(newObject);
            return true;
        }

        return false;
    }

    //
    // Other
    //

    private void ensureGameNotOver() {
        if (state == GameState.GameOver) {
            throw new IllegalStateException("Game over");
        }
    }

    private void finishMoveDown() {
        if (current.getRow() >= 0) {
            gameMap.absorb(current);
            selectNextBlock();
            purgeFullRows();
        } else {
            setState(GameState.GameOver);
        }
    }

    private void purgeFullRows() {
        int i = gameMap.getRows() - 1;
        while (i > 0 /* don't bother with the top row */) {
            if (gameMap.purgeRow(i)) {
                linesCleared++;
                level = linesCleared / LINES_PER_LEVEL;

                fireDeletingRow(i);
            } else {
                i--;
            }
        }
    }

    private MovingShape random() {
        BlockType blockType = BlockType.random();
        Shape shape = blockBuilder.create(blockType);
        return new MovingShape(shape, -shape.getRows(), (gameMap.getColumns() - shape.getColumns()) / 2);
    }

    private void selectNextBlock() {
        current = next == null ? random() : next;
        next = random();
        fireShapeChanged();
    }

    private boolean canChangeCurrent(MovingShape newShape) {
        return state == GameState.Started
                && gameMap.canMoveTo(newShape.getShapeDefinition(), newShape.getRow(), newShape.getColumn());
    }

    private void changeCurrentShape(MovingShape newShape) {
        MovingShape old = current;
        current = newShape;
        fireMoved(old);
    }
}
