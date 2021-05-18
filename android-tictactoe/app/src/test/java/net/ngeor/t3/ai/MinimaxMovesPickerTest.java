package net.ngeor.t3.ai;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.GameModelHolder;
import net.ngeor.t3.models.ImmutableGameModelImpl;
import net.ngeor.t3.models.Location;
import net.ngeor.t3.models.PlayerSymbol;
import net.ngeor.t3.settings.AIPlayerDefinition;
import net.ngeor.t3.settings.HumanPlayerDefinition;
import net.ngeor.t3.settings.PlayerDefinition;
import net.ngeor.t3.settings.PlayerDefinitions;
import net.ngeor.t3.settings.Settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Unit test for MinimaxMovesPicker.
 *
 * @author ngeor on 2/6/2017.
 */
class MinimaxMovesPickerTest {
    private GameModelHolder model;

    @BeforeEach
    void before() {
        PlayerDefinition first = new HumanPlayerDefinition(PlayerSymbol.X);
        PlayerDefinition second = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        Settings settings = new Settings(3, 3, false, new PlayerDefinitions(first, second));
        model = new GameModelHolder();
        model.setBackingModel(new ImmutableGameModelImpl(settings));
        model.start();
    }

    /**
     * This test proves the CPU will block this scenario.
     * Next move is CPU, CPU plays with O:
     * <p>
     * x| |X
     * O|X|
     * O|X|O
     * <p>
     * The correct move is to play on the top row.
     */
    @Test
    void shouldPreventLosing() {
        // arrange
        model.play(0, 0); // X
        model.play(1, 0); // O
        model.play(1, 1); // X
        model.play(2, 0); // O
        model.play(2, 1); // X
        model.play(2, 2); // O
        model.play(0, 2); // X

        MinimaxMovesPicker move = new MinimaxMovesPicker(() -> false, model, 2);

        // act
        List<Location> locations = move.pickMoves(model);

        // assert
        Location[] actual = locations.toArray(new Location[0]);
        Location[] expected = new Location[]{
                new Location(0, 1)
        };
        assertArrayEquals(expected, actual);
    }

    /**
     * This test proves the CPU will play on a corner
     * when the user starts in the middle.
     */
    @Test
    void shouldDefendCorner() {
        // arrange
        model.play(1, 1); // X

        MinimaxMovesPicker move = new MinimaxMovesPicker(() -> false, model, 2);

        // act
        List<Location> locations = move.pickMoves(model);

        // assert
        Location[] actual = locations.toArray(new Location[0]);
        Location[] expected = {
                new Location(0, 0),
                new Location(0, 2),
                new Location(2, 0),
                new Location(2, 2),
        };
        assertArrayEquals(expected, actual);
    }

    /**
     * X
     * O
     * X
     * ---
     * Expected:
     * X
     * OO
     * X
     * ---
     * Actual:
     * X
     * O
     * O X
     */
    @Test
    void shouldDefendAgainstDoubleThreat() {
        // arrange
        model.play(0, 0); // X
        model.play(1, 1); // O
        model.play(2, 2); // X
        MinimaxMovesPicker move = new MinimaxMovesPicker(() -> false, model, 3);

        // act
        List<Location> locations = move.pickMoves(model);

        // assert
        Location[] actual = locations.toArray(new Location[0]);
        Location[] expected = {
                new Location(0, 1),
                new Location(1, 0),
                new Location(1, 2),
                new Location(2, 1),
        };
        assertArrayEquals(expected, actual);
    }

    @Test
    void shouldStopCalculatingAfterVictory() {
        // arrange
        model.play(0, 0);
        model.play(1, 1);
        model.play(0, 2);
        MinimaxMovesPicker move = new MinimaxMovesPicker(() -> false, model, 3);

        // act
        List<Location> locations = move.pickMoves(model);

        // assert
        Location[] actual = locations.toArray(new Location[0]);
        Location[] expected = {
                new Location(0, 1)
        };
        assertArrayEquals(expected, actual);
    }
}
