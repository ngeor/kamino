package net.ngeor.t3.settings;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.PlayerSymbol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for AIPlayerDefinition.
 *
 * @author ngeor on 11/2/2018.
 */
class AIPlayerDefinitionTest {
    @Test
    void playerSymbolCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new AIPlayerDefinition(null, AILevel.EASY));
    }

    @Test
    void aiLevelCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new AIPlayerDefinition(PlayerSymbol.X, null));
    }

    @Test
    void getAILevel() {
        AIPlayerDefinition definition = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertEquals(AILevel.EASY, definition.getAILevel());
    }

    @Test
    void testToString() {
        AIPlayerDefinition definition = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertEquals("AIPlayerDefinition {X EASY}", definition.toString());
    }

    @Test
    void testEquals() {
        AIPlayerDefinition a = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        AIPlayerDefinition b = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertEquals(a, b);
    }

    @Test
    void testNullNotEquals() {
        AIPlayerDefinition a = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertNotEquals(a, null);
    }

    @Test
    void testDifferentSymbolNotEquals() {
        AIPlayerDefinition a = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        AIPlayerDefinition b = new AIPlayerDefinition(PlayerSymbol.O, AILevel.EASY);
        assertNotEquals(a, b);
    }

    @Test
    void testDifferentAILevelNotEquals() {
        AIPlayerDefinition a = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        AIPlayerDefinition b = new AIPlayerDefinition(PlayerSymbol.X, AILevel.MEDIUM);
        assertNotEquals(a, b);
    }

    @Test
    void testDifferentImplNotEquals() {
        AIPlayerDefinition a = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        HumanPlayerDefinition b = new HumanPlayerDefinition(PlayerSymbol.X);
        assertNotEquals(a, b);
    }

    @Test
    void testHashCode() {
        AIPlayerDefinition a = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        AIPlayerDefinition b = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void getPlayerSymbol() {
        AIPlayerDefinition definition = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertEquals(PlayerSymbol.X, definition.getPlayerSymbol());
    }
}