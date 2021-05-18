package net.ngeor.t3.settings;

import net.ngeor.t3.models.AILevel;
import net.ngeor.t3.models.PlayerSymbol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for HumanPlayerDefinition.
 *
 * @author ngeor on 11/2/2018.
 */
class HumanPlayerDefinitionTest {
    @Test
    void playerSymbolCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new HumanPlayerDefinition(null));
    }

    @Test
    void testToString() {
        HumanPlayerDefinition definition = new HumanPlayerDefinition(PlayerSymbol.O);
        assertEquals("HumanPlayerDefinition {O}", definition.toString());
    }

    @Test
    void testEquals() {
        HumanPlayerDefinition a = new HumanPlayerDefinition(PlayerSymbol.X);
        HumanPlayerDefinition b = new HumanPlayerDefinition(PlayerSymbol.X);
        assertEquals(a, b);
    }

    @Test
    void testNotEquals() {
        HumanPlayerDefinition a = new HumanPlayerDefinition(PlayerSymbol.X);
        HumanPlayerDefinition b = new HumanPlayerDefinition(PlayerSymbol.O);
        assertNotEquals(a, b);
    }

    @Test
    void testNullNotEquals() {
        HumanPlayerDefinition a = new HumanPlayerDefinition(PlayerSymbol.X);
        assertNotEquals(a, null);
    }

    @Test
    void testDifferentImplNotEquals() {
        HumanPlayerDefinition a = new HumanPlayerDefinition(PlayerSymbol.X);
        AIPlayerDefinition b = new AIPlayerDefinition(PlayerSymbol.X, AILevel.EASY);
        assertNotEquals(a, b);
    }

    @Test
    void testHashCode() {
        HumanPlayerDefinition a = new HumanPlayerDefinition(PlayerSymbol.X);
        HumanPlayerDefinition b = new HumanPlayerDefinition(PlayerSymbol.X);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void getPlayerSymbol() {
        HumanPlayerDefinition x = new HumanPlayerDefinition(PlayerSymbol.X);
        assertEquals(PlayerSymbol.X, x.getPlayerSymbol());
    }
}