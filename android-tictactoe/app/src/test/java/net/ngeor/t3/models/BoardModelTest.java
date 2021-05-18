package net.ngeor.t3.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for BoardModel.
 *
 * @author ngeor on 2/7/2017.
 */
class BoardModelTest {
    @Test
    void getRows() {
        BoardModel model = new BoardModel(3, 2);
        assertEquals(3, model.getRows());
    }

    @Test
    void getCols() {
        BoardModel model = new BoardModel(3, 2);
        assertEquals(2, model.getCols());
    }

    @Test
    void getTileState() {
        BoardModel model = new BoardModel(3, 3);
        assertNull(model.getTileState(0, 0));
    }

    @Test
    void playAt() {
        BoardModel model = new BoardModel(3, 3)
                .playAt(1, 1, PlayerSymbol.O);
        assertEquals(PlayerSymbol.O, model.getTileState(1, 1));
    }

    @Test
    void playAt_playerSymbolCannotBeNull() {
        BoardModel model = new BoardModel(3, 3);
        assertThrows(IllegalArgumentException.class, () -> model.playAt(1, 1, null));
    }

    @Test
    void playAt_cannotPlayOnTakenTile() {
        assertThrows(IllegalStateException.class, () -> new BoardModel(3, 3)
                .playAt(1, 1, PlayerSymbol.O)
                .playAt(1, 1, PlayerSymbol.X));
    }

    @Test
    void playAt_doesNotMutateOriginalModel() {
        BoardModel original = new BoardModel(3, 3);
        BoardModel modified = original.playAt(1, 1, PlayerSymbol.X);
        assertNull(original.getTileState(1, 1));
        assertEquals(PlayerSymbol.X, modified.getTileState(1, 1));
    }

    @Test
    void getTileStateByLocation() {
        BoardModel model = new BoardModel(3, 3)
                .playAt(1, 2, PlayerSymbol.X);
        assertEquals(PlayerSymbol.X, model.getTileState(new Location(1, 2)));
    }

    @Test
    void allLocations() {
        BoardModel model = new BoardModel(2, 2);
        List<Location> allLocations = model.allLocations();
        Location[] actual = allLocations.toArray(new Location[0]);
        Location[] expected = new Location[]{
                new Location(0, 0),
                new Location(0, 1),
                new Location(1, 0),
                new Location(1, 1),
        };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void emptyLocations() {
        BoardModel model = new BoardModel(2, 2)
                .playAt(0, 1, PlayerSymbol.O);
        List<Location> emptyLocations = model.emptyLocations();
        Location[] actual = emptyLocations.toArray(new Location[0]);
        Location[] expected = new Location[]{
                new Location(0, 0),
                new Location(1, 0),
                new Location(1, 1),
        };
        assertArrayEquals(expected, actual);
    }

    @Test
    void isBoardFull_OnFullBoard_ShouldBeTrue() {
        BoardModel model = new BoardModel(2, 2)
                .playAt(0, 0, PlayerSymbol.O)
                .playAt(0, 1, PlayerSymbol.X)
                .playAt(1, 0, PlayerSymbol.O)
                .playAt(1, 1, PlayerSymbol.X);
        assertTrue(model.isBoardFull());
    }

    @Test
    void isBoardFull_OnBoardWithSomeEmptyLocations_ShouldBeFalse() {
        BoardModel model = new BoardModel(2, 2)
                .playAt(0, 0, PlayerSymbol.O)
                .playAt(1, 0, PlayerSymbol.O)
                .playAt(1, 1, PlayerSymbol.X);
        assertFalse(model.isBoardFull());
    }
}
