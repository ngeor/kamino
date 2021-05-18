package net.ngeor.t3.settings;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.PlayerSymbol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Settings.
 *
 * @author ngeor on 2/11/2017.
 */
class SettingsTest {
    @Test
    void create() {
        // arrange
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);

        // act
        Settings settings = new Settings(3, 4, false, new PlayerDefinitions(ai, human));

        // assert
        assertEquals(3, settings.getRows());
        assertEquals(4, settings.getCols());
        assertEquals(new PlayerDefinitions(ai, human), settings.getPlayerDefinitions());
        assertFalse(settings.isInvisibleMode());
    }

    @Test
    void create_invisibleMode() {
        // arrange
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);

        // act
        Settings settings = new Settings(3, 3, true, new PlayerDefinitions(ai, human));

        // assert
        assertTrue(settings.isInvisibleMode());
    }

    @Test
    void create_playerDefinitionsCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Settings(3, 3, false, null));
    }

    @Test
    void testEquals() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        assertEquals(settings1, settings2);
    }

    @Test
    void testHashCode() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        assertEquals(settings1.hashCode(), settings2.hashCode());
    }

    @Test
    void testHashCode_differentInvisibleMode() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(3, 4, true, new PlayerDefinitions(ai, human));
        assertNotEquals(settings1.hashCode(), settings2.hashCode());
    }

    @Test
    void testNotEquals_Null() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        assertNotEquals(settings1, null);
    }

    @Test
    void testNotEquals_WrongType() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        assertNotEquals(settings1, ai);
    }

    @Test
    void testNotEquals_differentRows() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(4, 4, false, new PlayerDefinitions(ai, human));
        assertNotEquals(settings1, settings2);
    }

    @Test
    void testNotEquals_differentCols() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 4, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(3, 3, false, new PlayerDefinitions(ai, human));
        assertNotEquals(settings1, settings2);
    }

    @Test
    void testNotEquals_differentInvisibleMode() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 3, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(3, 3, true, new PlayerDefinitions(ai, human));
        assertNotEquals(settings1, settings2);
    }

    @Test
    void testNotEquals_differentPlayerDefinitions() {
        AIPlayerDefinition ai = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        HumanPlayerDefinition human = new HumanPlayerDefinition(PlayerSymbol.O);
        Settings settings1 = new Settings(3, 3, false, new PlayerDefinitions(ai, human));
        Settings settings2 = new Settings(3, 3, false, new PlayerDefinitions(human, ai));
        assertNotEquals(settings1, settings2);
    }
}