package net.ngeor.t3.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by ngeor on 2/4/2017.
 */
class PlayerSymbolTest {
    @Test
    void opponentOfXShouldBeO() {
        assertEquals(PlayerSymbol.O, PlayerSymbol.X.opponent());
    }

    @Test
    void opponentOfOShouldBeX() {
        assertEquals(PlayerSymbol.X, PlayerSymbol.O.opponent());
    }
}
