package net.ngeor.t3.settings;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.PlayerSymbol;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for PlayerDefinitions.
 *
 * @author ngeor on 11/2/2018.
 */
class PlayerDefinitionsTest {
    @Test
    void create() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions playerDefinitions = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );

        assertEquals(firstPlayerDefinition, playerDefinitions.getFirstPlayerDefinition());
        assertEquals(secondPlayerDefinition, playerDefinitions.getSecondPlayerDefinition());
    }

    @Test
    void create_firstPlayerDefinitionCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new PlayerDefinitions(null, new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY)));
    }

    @Test
    void create_secondPlayerDefinitionCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new PlayerDefinitions(new HumanPlayerDefinition(PlayerSymbol.X), null));
    }

    @Test
    void create_playersCannotBeEqual() {
        assertThrows(IllegalArgumentException.class, () ->
                new PlayerDefinitions(
                        new HumanPlayerDefinition(PlayerSymbol.X),
                        new HumanPlayerDefinition(PlayerSymbol.X)
                ));
    }

    @Test
    void create_playerSymbolsCannotBeEqual() {
        assertThrows(IllegalArgumentException.class, () ->
                new PlayerDefinitions(
                        new HumanPlayerDefinition(PlayerSymbol.X),
                        new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY)
                ));
    }

    @Test
    void getByPlayerSymbol_firstPlayer() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions playerDefinitions = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );

        // act
        PlayerDefinition playerDefinition = playerDefinitions.get(PlayerSymbol.X);

        // assert
        assertEquals(firstPlayerDefinition, playerDefinition);
    }

    @Test
    void getByPlayerSymbol_secondPlayer() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions playerDefinitions = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );

        // act
        PlayerDefinition playerDefinition = playerDefinitions.get(PlayerSymbol.O);

        // assert
        assertEquals(secondPlayerDefinition, playerDefinition);
    }

    @Test
    void getByPlayerSymbol_playerSymbolCannotBeNull() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions playerDefinitions = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );

        // act
        assertThrows(IllegalArgumentException.class, () ->

                playerDefinitions.get(null));
    }

    @Test
    void testEquals() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions a = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        PlayerDefinitions b = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        assertEquals(a, b);
    }

    @Test
    void testNotEquals_Null() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions a = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        assertNotEquals(a, null);
    }

    @Test
    void testNotEquals_DifferentType() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions a = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        assertNotEquals(a, Arrays.asList(firstPlayerDefinition, secondPlayerDefinition));
    }

    @Test
    void testNotEquals_FirstIsDifferent() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions a = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        PlayerDefinitions b = new PlayerDefinitions(
                secondPlayerDefinition,
                firstPlayerDefinition
        );

        assertNotEquals(a, b);
    }

    @Test
    void testNotEquals_SecondIsDifferent() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        AIPlayerDefinition secondPlayerDefinitionMedium = new AIPlayerDefinition(PlayerSymbol.O, AILevel.MEDIUM);
        PlayerDefinitions a = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        PlayerDefinitions b = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinitionMedium
        );

        assertNotEquals(a, b);
    }

    @Test
    void testHashCode() {
        HumanPlayerDefinition firstPlayerDefinition = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition secondPlayerDefinition = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        PlayerDefinitions a = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );
        PlayerDefinitions b = new PlayerDefinitions(
                firstPlayerDefinition,
                secondPlayerDefinition
        );

        assertEquals(a.hashCode(), b.hashCode());
    }
}
